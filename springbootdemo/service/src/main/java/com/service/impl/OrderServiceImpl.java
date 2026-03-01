package com.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constant.OrderConstants;
import com.repository.mapper.OrderMapper;
import com.repository.mapper.ProductMapper;
import com.service.OrderService;
import com.utils.OrderDistributedLock;
import domain.entity.Orders;
import domain.entity.Product;
import domain.order.*;
import domain.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.constant.OrderConstants.*;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final OrderDistributedLock orderDistributedLock;

    public OrderServiceImpl(OrderMapper orderMapper, ProductMapper productMapper, OrderDistributedLock orderDistributedLock) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.orderDistributedLock = orderDistributedLock;
    }

    /**
     * 创建订单
     *
     * @param userId         用户ID
     * @param orderCreateDTO 创建订单参数
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)// 事务控制, 出现异常回滚
    public Result<OrderVO> createOrder(Long userId, OrderCreateDTO orderCreateDTO) {

        if (userId == null) {
            return Result.error(ERROR_CODE_LOGIN_REQUIRED, OrderConstants.MSG_LOGIN_REQUIRED);
        }

        // 1. 参数基础校验
        List<Long> productIds = orderCreateDTO.getCartItemIds();
        if (productIds == null || productIds.isEmpty()) {
            return Result.error(ERROR_CODE_SELECT_PRODUCT, OrderConstants.MSG_SELECT_PRODUCT);
        }

        List<OrderItem> items = orderCreateDTO.getItems();
        if (items == null || items.isEmpty()) {
            return Result.error(ERROR_CODE_SELECT_PRODUCT, OrderConstants.MSG_SELECT_PRODUCT);
        }

        // 2. 构建分布式锁Key(粒度:用户ID+商品ID组合,防止同一用户并发下单同一商品)
        // 对商品ID排序,避免不同顺序的商品列表生成不同锁Key
        String sortedProductIds = productIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(":"));
        String lockKey = String.format("%s%s:%s", OrderConstants.ORDER_LOCK_PREFIX, userId, sortedProductIds);

        String lockValue = null;

        try {
            // 3. 获取分布式锁(最多重试5秒,锁超时30秒)
            lockValue = orderDistributedLock.tryLock(lockKey, 30000, 5000, 5);
            if (lockValue == null) {
                return Result.error(ERROR_CODE_DUPLICATE_ORDER, MSG_DUPLICATE_ORDER);
            }

            // 4. 批量查询商品
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Product::getId, productIds);
            List<Product> products = productMapper.selectList(queryWrapper);
            if (products == null || products.isEmpty()) {
                return Result.error(ERROR_CODE_PRODUCT_NOT_EXIST, MSG_PRODUCT_NOT_EXIST);
            }

            // 转为Map,方便快速查询
            Map<Long, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, product -> product));

            // 5. 计算金额+校验库存
            BigDecimal payAmount = BigDecimal.ZERO;
            BigDecimal totalAmount = BigDecimal.ZERO;

            Map<Long, Integer> stockDeductMap = new HashMap<>();

            for (OrderItem item : items) {
                Long productId = item.getProductId();
                Integer quantity = item.getQuantity();

                // 商品不存在校验
                if (productId == null || quantity == null || quantity <= 0) {
                    rollbackStock(stockDeductMap);
                    return Result.error(ERROR_CODE_PRODUCT_QUANTITY_INVALID, MSG_PRODUCT_QUANTITY_INVALID);
                }

                Product product = productMap.get(productId);
                if (product == null) {
                    rollbackStock(stockDeductMap);
                    return Result.error(ERROR_CODE_PRODUCT_NOT_EXIST, String.format("商品ID: %s %s", productId, MSG_PRODUCT_NOT_EXIST ));
                }

                // 库存校验
                if (product.getStock() < quantity) {
                    rollbackStock(stockDeductMap);
                    return Result.error(ERROR_CODE_STOCK_INSUFFICIENT, String.format("商品: %s %s,当前库存:%s",
                            product.getName(), MSG_STOCK_INSUFFICIENT , product.getStock()));
                }

                // 计算金额
                BigDecimal itemAmount = product.getPrice().multiply(new BigDecimal(quantity));
                payAmount = payAmount.add(itemAmount);
                totalAmount = totalAmount.add(itemAmount);

                Integer updateCount = productMapper.updateStock(productId, quantity);
                if (updateCount == null || updateCount <= 0) {
                    rollbackStock(stockDeductMap);
                    return Result.error(ERROR_CODE_STOCK_DEDUCT_FAILED, String.format("商品: %s %s", product.getName(), OrderConstants.MSG_STOCK_DEDUCT_FAILED));
                }
            }

            // 6. 构建订单数据
            Orders order = new Orders();
            String orderNo = OrderConstants.ORDER_NO_PREFIX + System.currentTimeMillis() + (int) (Math.random() * 10000);
            BeanUtils.copyProperties(orderCreateDTO, order);
            order.setOrderNo(orderNo);
            order.setPayAmount(payAmount);
            order.setTotalAmount(totalAmount);
            order.setOrderStatus(0);
            order.setUserId(userId);

            // 7. 拼接订单商品信息
            List<OrderItemVO> productInfoList = new ArrayList<>();
            for (OrderItem item : items) {
                Product product = productMap.get(item.getProductId());
                OrderItemVO itemVO = new OrderItemVO();
                itemVO.setProductId(product.getId());
                itemVO.setProductName(product.getName());
                itemVO.setProductImage(product.getMainImage());
                itemVO.setPrice(product.getPrice());
                itemVO.setQuantity(item.getQuantity());
                itemVO.setTotalPrice(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
                productInfoList.add(itemVO);
            }
            // 转为JSON字符串
            String orderProductInfo = JSON.toJSONString(productInfoList);
            order.setInfo(orderProductInfo);

            // 8. 插入订单数据
            int insertCount = orderMapper.insert(order);
            if (insertCount <= 0) {
                rollbackStock(stockDeductMap);
                return Result.error(ERROR_CODE_ORDER_CREATE_FAILED , OrderConstants.MSG_ORDER_CREATE_FAILED);
            }

            // 9. 构建订单VO
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setItems(productInfoList);
            return Result.success(orderVO);

        } catch (Exception e) {
            // 捕获所有异常,保证事务回滚
            log.error("创建订单异常,用户ID: {},异常信息: {}", userId, e); // 错误日志
            return Result.error(ERROR_CODE_ORDER_CREATE_EXCEPTION,MSG_ORDER_CREATE_EXCEPTION);
        } finally {
            // 10. 释放分布式锁
            if (lockValue != null) {
                orderDistributedLock.releaseLock(lockKey, lockValue);
            }
        }
    }

    /**
     * 库存回滚
     */
    private void rollbackStock(Map<Long, Integer> stockDeductMap) {
        if (stockDeductMap == null || stockDeductMap.isEmpty()) {
            return;
        }
        for (Map.Entry<Long, Integer> entry : stockDeductMap.entrySet()) {
            try {
                // 库存回滚: 扣减的数量加回去
                productMapper.rollbackStock(entry.getKey(), entry.getValue());
                log.info("商品ID:{} 库存回滚 {} 件", entry.getKey(), entry.getValue());
            } catch (Exception e) {
                log.error("商品ID:{} 库存回滚失败", entry.getKey(), e);
            }
        }
    }


    /**
     * 获取订单列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param status   订单状态 0:待付款 1:待发货 2:待收货 3:已完成 4:已取消
     */
    @Override
    public Result<OrderPageVO> getOrderList(Long userId, Integer pageNum, Integer pageSize, Integer status) {
        // 1. 校验入参
        if (userId == null) {
            return Result.error(ERROR_CODE_USER_ID_REQUIRED, OrderConstants.MSG_USER_ID_REQUIRED);
        }

        if (pageNum == null || pageNum <= 0) {
            return Result.error(ERROR_CODE_PAGE_NUM_INVALID, OrderConstants.MSG_PAGE_NUM_INVALID);
        }

        if (pageSize == null || pageSize <= 0) {
            return Result.error(ERROR_CODE_PAGE_SIZE_INVALID , OrderConstants.MSG_PAGE_SIZE_INVALID);
        }

        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);


        // 查询订单状态
        // 如果订单状态不为5 则查询所有状态
        if (status != null && status != 5) {
            queryWrapper.eq(Orders::getOrderStatus, status);
        }


        // 3. 执行分页查询
        Page<Orders> ordersPage = this.baseMapper.selectPage(page, queryWrapper);

        // 4. 转为VO
        List<OrderVO> orderVOList = ordersPage.getRecords().stream()
                .map(order -> {
                    OrderVO orderVO = new OrderVO();
                    BeanUtils.copyProperties(order, orderVO);

                    orderVO.setUserId(order.getUserId());
                    orderVO.setStatus(order.getOrderStatus());
                    orderVO.setTotalAmount(order.getTotalAmount());

                    // 安全解析JSON，兼容对象和数组格式
                    List<OrderItemVO> itemVOList = new ArrayList<>();
                    if (order.getInfo() != null && !order.getInfo().isEmpty()) {
                        try {
                            // 先尝试解析为数组
                            itemVOList = JSON.parseArray(order.getInfo(), OrderItemVO.class);
                        } catch (Exception e1) {
                            try {
                                // 数组解析失败,尝试解析为单个对象
                                OrderItemVO singleItem = JSON.parseObject(order.getInfo(), OrderItemVO.class);
                                itemVOList.add(singleItem);
                                log.info("订单{}的商品信息为单个对象,已兼容处理", order.getOrderNo());
                            } catch (Exception e2) {
                                log.warn("解析订单{}商品信息失败", order.getOrderNo(), e2);
                                itemVOList = new ArrayList<>();
                            }
                        }
                    }
                    orderVO.setItems(itemVOList);
                    return orderVO;
                })
                .collect(Collectors.toList());

        // 5. 构建VO的分页对象
        OrderPageVO orderList = new OrderPageVO();
        orderList.setTotal(ordersPage.getTotal());
        orderList.setPages(ordersPage.getPages());
        orderList.setCurrent((int) ordersPage.getCurrent());
        orderList.setSize((int) ordersPage.getSize());
        orderList.setRecords(orderVOList);

        // 6. 返回结果
        return Result.success(orderList);
    }

    /**
     * 获取订单详情
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     */
    @Override
    public Result<OrderVO> getOrder(Long userId, String orderNo) {

        if (userId == null) {
            log.warn("用户ID:{}不能为空", userId);
            return Result.error(ERROR_CODE_USER_ID_REQUIRED, OrderConstants.MSG_USER_ID_REQUIRED);
        }

        if (orderNo == null) {
            log.warn("用户ID:{},订单号不能为空", userId);
            return Result.error(ERROR_CODE_ORDER_NO_REQUIRED, OrderConstants.MSG_ORDER_NO_REQUIRED);
        }

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        queryWrapper.eq(Orders::getOrderNo, orderNo);
        Orders order = orderMapper.selectOne(queryWrapper);
        if (order == null) {
            log.warn("订单:{}不存在", orderNo);
            return Result.error(ERROR_CODE_ORDER_NOT_EXIST, OrderConstants.MSG_ORDER_NOT_EXIST);
        }

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        orderVO.setUserId(order.getUserId());
        orderVO.setStatus(order.getOrderStatus());
        orderVO.setTotalAmount(order.getTotalAmount());

        // 安全解析JSON，兼容对象和数组格式
        List<OrderItemVO> itemVOList = new ArrayList<>();
        if (order.getInfo() != null && !order.getInfo().isEmpty()) {
            try {
                itemVOList = JSON.parseArray(order.getInfo(), OrderItemVO.class);
            } catch (Exception e1) {
                try {
                    OrderItemVO singleItem = JSON.parseObject(order.getInfo(), OrderItemVO.class);
                    itemVOList.add(singleItem);
                    log.info("订单{}的商品信息为单个对象,已兼容处理", order.getOrderNo());
                } catch (Exception e2) {
                    log.warn("解析订单{}商品信息失败", order.getOrderNo(), e2);
                }
            }
        }
        orderVO.setItems(itemVOList);

        return Result.success(orderVO);
    }

    /**
     * 取消订单
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     * @param reason  取消原因
     */
    @Override
    public Result<String> cancelOrder(Long userId, String orderNo, String reason) {

        if (userId == null) {
            log.warn(OrderConstants.MSG_USER_ID_REQUIRED);
            return Result.error(ERROR_CODE_USER_ID_REQUIRED, OrderConstants.MSG_USER_ID_REQUIRED);
        }

        if (!StringUtils.hasText(orderNo)) {
            log.warn(OrderConstants.MSG_ORDER_NO_REQUIRED);
            return Result.error(ERROR_CODE_ORDER_NO_REQUIRED, OrderConstants.MSG_ORDER_NO_REQUIRED);
        }

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId)
                    .eq(Orders::getOrderNo, orderNo);
        Orders order = orderMapper.selectOne(queryWrapper);
        if (order == null) {
            log.warn("订单{}不存在,用户ID:{}", orderNo, userId);
            return Result.error(ERROR_CODE_ORDER_NOT_EXIST, OrderConstants.MSG_ORDER_NOT_EXIST);
        }

        if (!Objects.equals(order.getOrderStatus(), OrderConstants.ORDER_STATUS_PENDING_PAYMENT)) {
            log.warn("订单{}状态错误,当前状态:{},仅待付款订单可取消",
                    order.getOrderNo(), order.getOrderStatus());
            return Result.error(ERROR_CODE_ONLY_PENDING_PAYMENT_CANCEL, OrderConstants.MSG_ONLY_PENDING_PAYMENT_CANCEL);
        }

        // 4. 回滚库存
        if (StringUtils.hasText(order.getInfo())) {
            try {
                List<OrderItemVO> itemVOList = JSON.parseArray(order.getInfo(), OrderItemVO.class);
                for (OrderItemVO item : itemVOList) {
                    productMapper.rollbackStock(item.getProductId(), item.getQuantity());
                }
                log.info("订单{}取消,库存已回滚", orderNo);
            } catch (Exception e) {
                log.error("订单{}取消时库存回滚失败", orderNo, e);
            }
        }

        // 5. 更新订单状态
        order.setOrderStatus(OrderConstants.ORDER_STATUS_CANCELLED);
        order.setReason(reason);

        int update = orderMapper.updateById(order);
        if (update == 0) {
            log.warn("订单{}取消失败", order.getOrderNo());
            return Result.error(ERROR_CODE_ORDER_CANCEL_FAILED, OrderConstants.MSG_ORDER_CANCEL_FAILED);
        }

        return Result.success(SUCCESS_CODE_ORDER_CANCEL,SUCCESS_MSG_ORDER_CANCEL);
    }

    /**
     * 删除订单
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     */
    @Override
    public Result<String> deleteOrder(Long userId, String orderNo) {

        if (userId == null) {
            log.warn(OrderConstants.MSG_USER_ID_REQUIRED);
            return Result.error(ERROR_CODE_USER_ID_REQUIRED, OrderConstants.MSG_USER_ID_REQUIRED);
        }

        if (!StringUtils.hasText(orderNo)) {
            log.warn(OrderConstants.MSG_ORDER_NO_REQUIRED);
            return Result.error(ERROR_CODE_ORDER_NO_REQUIRED, OrderConstants.MSG_ORDER_NO_REQUIRED);
        }

        // 仅允许删除已完成或已取消的订单
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId)
                    .eq(Orders::getOrderNo, orderNo);
        Orders order = orderMapper.selectOne(queryWrapper);

        if (order == null) {
            log.warn("订单{}不存在,用户ID:{}", orderNo, userId);
            return Result.error(ERROR_CODE_ORDER_NOT_EXIST, OrderConstants.MSG_ORDER_NOT_EXIST);
        }

        // 3. 状态校验（仅已完成/已取消可删除）
        if (!Objects.equals(order.getOrderStatus(), OrderConstants.ORDER_STATUS_COMPLETED)
                && !Objects.equals(order.getOrderStatus(), OrderConstants.ORDER_STATUS_CANCELLED)) {
            log.warn("订单{}状态错误,当前状态:{},仅已完成或已取消的订单可删除",
                    order.getOrderNo(), order.getOrderStatus());
            return Result.error(ERROR_CODE_ONLY_FINISHED_DELETABLE, OrderConstants.MSG_ONLY_FINISHED_DELETABLE);
        }

        // 4. 删除订单
        int delete = orderMapper.delete(queryWrapper);
        if (delete == 0) {
            log.warn("订单{}删除失败", order.getOrderNo());
            return Result.error(ERROR_CODE_ORDER_DELETE_FAILED, OrderConstants.MSG_ORDER_DELETE_FAILED);
        }
        return Result.success(SUCCESS_CODE_ORDER_DELETE,SUCCESS_MSG_ORDER_DELETE);
    }
}
