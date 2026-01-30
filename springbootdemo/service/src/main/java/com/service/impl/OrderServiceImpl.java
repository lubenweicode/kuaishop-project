package com.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.OrderMapper;
import com.mapper.ProductMapper;
import com.service.OrderService;
import com.utils.OrderDistributedLock;
import generator.domain.entity.Orders;
import generator.domain.entity.Product;
import generator.domain.demo.Result;
import generator.domain.order.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
     * @param userId 用户ID
     * @param orderCreateDTO 创建订单参数
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)// 事务控制, 出现异常回滚
    public Result<OrderVO> createOrder(Long userId,OrderCreateDTO orderCreateDTO) {

        if (userId == null) {
            return Result.error(500, "请先登录");
        }

        // 1. 参数基础校验
        List< Long> productIds = orderCreateDTO.getCartItemIds();
        if (productIds == null || productIds.isEmpty()) {
            return Result.error(500, "请选择商品");
        }

        List<OrderItem> items = orderCreateDTO.getItems();
        if (items == null || items.isEmpty()) {
            return Result.error(500, "请选择商品");
        }

        // 2. 构建分布式锁Key(粒度:用户ID+商品ID组合,防止同一用户并发下单同一商品)
        // 对商品ID排序,避免不同顺序的商品列表生成不同锁Key
        String sortedProductIds = productIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(":"));
        String lockKey = String.format("order:lock:%s:%s", userId, sortedProductIds);

        String lockValue = null;

        try{
            // 3. 获取分布式锁(最多重试5秒,锁超时30秒)
            lockValue = orderDistributedLock.tryLock(lockKey, 30000, 5000, 5);
            if (lockValue == null) {
                return Result.error(500, "请勿重复下单");
            }

            // 4. 批量查询商品
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Product::getId, productIds);
            List<Product> products = productMapper.selectList(queryWrapper);
            if (products == null || products.isEmpty()) {
                return Result.error(500, "商品不存在");
            }

            // 转为Map,方便快速查询
            Map<Long, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, product -> product));

            // 5. 计算金额+校验库存
            BigDecimal payAmount = BigDecimal.ZERO;
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (OrderItem item : items) {
                Long productId = item.getProductId();
                Integer quantity = item.getQuantity();
                Product product = productMap.get(productId);

                // 商品不存在校验
                if (product == null){
                    return Result.error(500, "商品ID: "+productId+" 不存在");
                }

                // 库存校验(防止超卖)
                if (product.getStock() < quantity) {
                    return Result.error(500, "商品: "+product.getName()+" 库存不足,当前库存:"+product.getStock());
                }

                // 计算金额
                BigDecimal itemAmount = product.getPrice().multiply(new BigDecimal(quantity));
                payAmount = payAmount.add(itemAmount);
                totalAmount = totalAmount.add(itemAmount);

                Integer updateCount = productMapper.updateStock(productId, quantity);
                if(updateCount == null ||  updateCount <= 0){
                    return Result.error(500,"商品: "+product.getName()+" 库存扣减失败,请重试");
                }

            }

            // 6. 构建订单数据
            Orders order = new Orders();
            String orderNo = "QS" + System.currentTimeMillis() + (int)(Math.random() * 10000);
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
                return Result.error(500, "创建订单失败,请稍后重试");
            }

            // 9. 构建订单VO
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            return Result.success(orderVO);

        }catch (Exception e){
            // 捕获所有异常,保证事务回滚
            log.error("创建订单异常,用户ID: {},异常信息: {}", userId, e); // 错误日志
            return Result.error(500, "创建订单异常,请稍后重试");
        }finally {
            // 10. 释放分布式锁
            if (lockValue != null){
                orderDistributedLock.releaseLock(lockKey, lockValue);
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
            return Result.error(400, "用户ID不能为空");
        }

        if(pageNum == null || pageNum <= 0){
            return Result.error(400, "页码不能小于1");
        }

        if(pageSize == null || pageSize <= 0){
            return Result.error(400, "页大小不能小于1");
        }

        IPage<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);

        if(status != null){
            queryWrapper.eq(Orders::getOrderStatus, status);
        }

        // 3. 执行分页查询
        IPage<Orders> ordersPage = orderMapper.selectPage(page, queryWrapper);
        List<Orders> orders = Optional.ofNullable(ordersPage.getRecords()).orElse(Collections.emptyList());

        // 4. 转为VO
        List<OrderVO> orderVOList = orders.stream()
                .map(order -> {
                    OrderVO orderVO = new OrderVO();
                    BeanUtils.copyProperties(order, orderVO);

                    orderVO.setUserId(order.getUserId());
                    orderVO.setStatus(order.getOrderStatus());
                    orderVO.setTotalAmount(order.getTotalAmount());
                    orderVO.setStatus(order.getOrderStatus());
                    // 安全解析JSON，指定泛型
                    if (order.getInfo() != null && !order.getInfo().isEmpty()) {
                        try {
                            com.alibaba.fastjson.parser.ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
                            List<OrderItemVO> orderItemVOList = JSON.parseArray(
                                    order.getInfo(),
                                    OrderItemVO.class
                            );
                            orderVO.setItems(orderItemVOList);
                        } catch (Exception e) {
                            log.warn("解析订单{}商品信息失败", order.getOrderNo(), e);
                            orderVO.setItems(new ArrayList<>());
                        }
                    } else {
                        orderVO.setItems(new ArrayList<>());
                    }
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

    @Override
    public Result<OrderVO> getOrder(Long userId,String orderNo) {

        if(userId == null){
            log.warn("用户ID不能为空");
            return Result.error(400, "用户ID不能为空");
        }

        if(orderNo == null){
            log.warn("订单号不能为空");
            return Result.error(400, "订单号不能为空");
        }

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        queryWrapper.eq(Orders::getOrderNo, orderNo);
        Orders order = orderMapper.selectOne(queryWrapper);
        if(order == null){
            log.warn("订单不存在");
            return Result.error(404, "订单不存在");
        }

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        orderVO.setUserId(order.getUserId());
        orderVO.setStatus(order.getOrderStatus());
        orderVO.setTotalAmount(order.getTotalAmount());

        if(order.getInfo() != null && !order.getInfo().isEmpty()){
            try{
                List<OrderItemVO> orderItemVOList = JSON.parseArray(order.getInfo(), OrderItemVO.class);
                orderVO.setItems(orderItemVOList);
            }catch (Exception e){
                log.warn("解析订单{}商品信息失败", order.getOrderNo(), e);
                orderVO.setItems(new ArrayList<>());
            }
        }else{
            orderVO.setItems(new ArrayList<>());
        }

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

        if(userId == null){
            log.warn("用户ID不能为空");
            return Result.error(400, "用户ID不能为空");
        }

        if(orderNo == null){
            log.warn("订单号不能为空");
            return Result.error(400, "订单号不能为空");
        }

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        queryWrapper.eq(Orders::getOrderNo, orderNo);
        Orders order = orderMapper.selectOne(queryWrapper);
        if(order == null){
            log.warn("订单不存在");
            return Result.error(404, "订单不存在");
        }

        if(order.getOrderStatus() != 0){
            log.warn("订单{}状态错误", order.getOrderNo());
            return Result.error(400, "订单状态错误");
        }

        order.setOrderStatus(4);
        order.setReason(reason);

        int update = orderMapper.update(order, queryWrapper);
        if(update == 0){
            log.warn("订单{}取消失败", order.getOrderNo());
            return Result.error(500, "订单取消失败");
        }

        return Result.success("订单取消成功");
    }

    /**
     * 删除订单
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     */
    @Override
    public Result<String> deleteOrder(Long userId, String orderNo) {

        if(userId == null){
            log.warn("用户ID不能为空");
            return Result.error(400, "用户ID不能为空");
        }

        if(orderNo == null){
            log.warn("订单号不能为空");
            return Result.error(400, "订单号不能为空");
        }

        // 仅允许删除已完成或已取消的订单
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        queryWrapper.eq(Orders::getOrderNo, orderNo);
        Orders order = orderMapper.selectOne(queryWrapper);

        if(order.getOrderStatus() != 3 && order.getOrderStatus() != 4){
            log.warn("订单{}状态错误", order.getOrderNo());
            return Result.error(400, "订单状态错误");
        }

        int delete = orderMapper.delete(queryWrapper);
        if(delete == 0){
            log.warn("订单{}删除失败", order.getOrderNo());
            return Result.error(500, "订单删除失败");
        }
        return Result.success("订单删除成功");
    }
}
