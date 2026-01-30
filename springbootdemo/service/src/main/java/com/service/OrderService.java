package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import generator.domain.entity.Orders;
import generator.domain.demo.Result;
import generator.domain.order.OrderCreateDTO;
import generator.domain.order.OrderPageVO;
import generator.domain.order.OrderVO;

public interface OrderService extends IService<Orders> {

    Result<OrderVO> createOrder(Long userId,OrderCreateDTO orderVO);

    Result<OrderPageVO> getOrderList(Long userId, Integer pageNum, Integer pageSize, Integer status);

    Result<OrderVO> getOrder(Long userId,String orderNo);

    Result<String> cancelOrder(Long userId, String orderNo, String reason);

    Result<String> deleteOrder(Long userId, String orderNo);
}
