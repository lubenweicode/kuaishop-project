package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import domain.entity.Orders;
import domain.order.OrderCreateDTO;
import domain.order.OrderPageVO;
import domain.order.OrderVO;
import domain.response.Result;

public interface OrderService extends IService<Orders> {

    Result<OrderVO> createOrder(Long userId, OrderCreateDTO orderVO);

    Result<OrderPageVO> getOrderList(Long userId, Integer pageNum, Integer pageSize, Integer status);

    Result<OrderVO> getOrder(Long userId, String orderNo);

    Result<String> cancelOrder(Long userId, String orderNo, String reason);

    Result<String> deleteOrder(Long userId, String orderNo);
}
