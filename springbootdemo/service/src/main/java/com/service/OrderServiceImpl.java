package com.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.OrderMapper;
import com.mapper.ProductMapper;
import com.utils.OrderDistributedLock;
import generator.domain.Entity.Orders;
import generator.domain.Entity.Product;
import generator.domain.demo.Result;
import generator.domain.order.OrderCreateDTO;
import generator.domain.order.OrderItem;
import generator.domain.order.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService{

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
            StringBuilder orderProductInfo = new StringBuilder();
            for (OrderItem item : items) {
                Product product = productMap.get(item.getProductId());
                orderProductInfo.append("商品名称:").append(product.getName())
                        .append(";商品价格:").append(product.getPrice())
                        .append(";商品数量:").append(item.getQuantity())
                        .append(";");
            }
            order.setInfo(orderProductInfo.toString());

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
            log.error("创建订单异常,用户ID: {},异常信息: {}", userId, e.getMessage()); // 错误日志
            return Result.error(500, "创建订单异常,请稍后重试");
        }finally {
            // 10. 释放分布式锁
            if (lockValue != null){
                orderDistributedLock.releaseLock(lockKey, lockValue);
            }
        }
    }
}
