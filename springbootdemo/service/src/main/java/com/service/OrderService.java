package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import generator.domain.Entity.Orders;
import generator.domain.demo.Result;
import generator.domain.order.OrderCreateDTO;
import generator.domain.order.OrderVO;

public interface OrderService extends IService<Orders> {

    Result<OrderVO> createOrder(Long userId,OrderCreateDTO orderVO);

}
