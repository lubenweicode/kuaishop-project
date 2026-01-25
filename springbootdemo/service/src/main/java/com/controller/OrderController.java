package com.controller;


import com.service.OrderService;
import com.utils.JwtUtil;
import generator.domain.demo.Result;
import generator.domain.order.OrderCreateDTO;
import generator.domain.order.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final JwtUtil jwtUtil;
    private final OrderService orderService;

    public OrderController(JwtUtil jwtUtil, OrderService orderService) {
        this.jwtUtil = jwtUtil;
        this.orderService = orderService;
    }
    /**
     * 创建订单
     */
    @PostMapping
    public Result<OrderVO> createOrder(HttpServletRequest request, @RequestBody OrderCreateDTO orderCreateDTO) {
        Long userId = jwtUtil.getUserIdFromToken(request);
        return orderService.createOrder(userId,orderCreateDTO);
    }
}
