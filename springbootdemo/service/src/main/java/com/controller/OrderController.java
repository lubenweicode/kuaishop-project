package com.controller;

import com.service.OrderService;
import generator.domain.context.UserContext;
import generator.domain.demo.Result;
import generator.domain.order.OrderCreateDTO;
import generator.domain.order.OrderPageVO;
import generator.domain.order.OrderVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     * @param orderCreateDTO 创建订单参数
     */
    @PostMapping
    public Result<OrderVO> createOrder(@RequestBody OrderCreateDTO orderCreateDTO){
        Long userId = UserContext.getUserId();
        return orderService.createOrder(userId,orderCreateDTO);
    }

    /**
     * 获取订单列表
     * @param page 页码
     * @param pageSize 页大小
     * @param status 订单状态 0:待付款 1:待发货 2:待收货 3:已完成 4:已取消
     */
    @GetMapping
    public Result<OrderPageVO> getOrders(@Param("page")Integer page, @Param("size")Integer pageSize, @Param("status") Integer status){
        Long userId = UserContext.getUserId();
        return orderService.getOrderList(userId,page,pageSize,status);
    }

    /**
     * 获取订单详情
     * @param orderNo 订单编号
     */
    @GetMapping("/{orderNo}")
    public Result<OrderVO> getOrder(@PathVariable String orderNo){
        Long userId = UserContext.getUserId();
        return orderService.getOrder(userId,orderNo);
    }

    /**
     * 取消订单
     * @param orderNo 订单编号
     * @param reason 取消原因
     */
    @PutMapping("/{orderNo}/cancel")
    public Result<String> cancelOrder(@PathVariable String orderNo,@RequestParam(name = "reason",required = false)String reason){
        Long userId = UserContext.getUserId();
        return orderService.cancelOrder(userId,orderNo,reason);
    }

    /**
     * 删除订单
     * @param orderNo 订单编号
     */
    @DeleteMapping("/{orderNo}")
    public Result<String> deleteOrder(@PathVariable String orderNo){
        Long userId = UserContext.getUserId();
        return orderService.deleteOrder(userId,orderNo);
    }
}
