package com.controller;

import com.service.AdminService;
import generator.domain.context.UserContext;
import generator.domain.entity.SeckillActivity;
import generator.domain.order.OrderPageVO;
import generator.domain.product.ProductDTO;
import generator.domain.product.ProductVO;
import generator.domain.response.Result;
import generator.domain.seckill.SeckillActivityDTO;
import generator.domain.statistics.Statistics;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    /**
     * 添加商品
     *
     * @param productDTO
     * @return
     */
    @PostMapping("/products")
    public Result<ProductVO> addProducts(@RequestBody ProductDTO productDTO) {
        Long userId = UserContext.getUserId();
        return adminService.addProducts(userId, productDTO);
    }

    /**
     * 获取订单列表
     *
     * @param page      页码
     * @param size      每页数量
     * @param orderNo   订单编号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param status    订单状态
     * @return 订单列表
     */
    @GetMapping("/orders")
    public Result<OrderPageVO> getOrders(@RequestParam(value = "page", required = false) Integer page,
                                         @RequestParam(value = "size", required = false) Integer size,
                                         @RequestParam(value = "orderNo", required = false) String orderNo,
                                         @RequestParam(value = "startTime", required = false) String startTime,
                                         @RequestParam(value = "endTime", required = false) String endTime,
                                         @RequestParam(value = "status", required = false) Integer status) {
        Long userId = UserContext.getUserId();
        return adminService.getOrderList(userId, page, size, orderNo, startTime, endTime, status);
    }

    /**
     * 发货
     *
     * @param orderNo 订单编号
     * @return 发货结果
     */
    @PutMapping("/orders/{orderNo}/ship")
    public Result<String> shipOrder(@PathVariable String orderNo,
                                    @RequestParam(value = "logisticsCompany", required = true) String logisticsCompany,
                                    @RequestParam(value = "logisticsNo", required = true) String logisticsNo) {
        Long userId = UserContext.getUserId();
        return adminService.shipOrder(userId, orderNo, logisticsCompany, logisticsNo);
    }

    /**
     * 获取销售统计数据
     *
     * @param type      统计类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计数据
     */
    @GetMapping("/statistics/sales")
    public Result<Statistics> getSalesStatistics(@RequestParam(value = "type", required = false) String type,
                                                 @RequestParam(value = "startTime", required = false) String startTime,
                                                 @RequestParam(value = "endTime", required = false) String endTime) {
        Long userId = UserContext.getUserId();
        return adminService.getSalesStatistics(userId, type, startTime, endTime);
    }

    /**
     * 添加秒杀活动
     *
     * @param seckillActivity 秒杀活动
     * @return
     */
    @PostMapping("/seckill/activity")
    public Result<SeckillActivity> addSeckillActivity(@RequestBody SeckillActivityDTO seckillActivity) {
        Long userId = UserContext.getUserId();
        return adminService.addSeckillActivity(userId, seckillActivity);
    }

}
