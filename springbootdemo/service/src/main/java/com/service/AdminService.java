package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import domain.entity.SeckillActivity;
import domain.entity.User;
import domain.order.OrderPageVO;
import domain.product.ProductDTO;
import domain.product.ProductVO;
import domain.response.Result;
import domain.seckill.SeckillActivityDTO;
import domain.statistics.Statistics;

public interface AdminService extends IService<User> {
    Result<ProductVO> addProducts(Long userId, ProductDTO productDTO);

    Result<OrderPageVO> getOrderList(Long userId, Integer page, Integer size, String orderNo, String startTime, String endTime, Integer status);

    Result<String> shipOrder(Long userId, String orderNo, String logisticsCompany, String logisticsNo);

    Result<Statistics> getSalesStatistics(Long userId, String type, String startTime, String endTime);

    Result<SeckillActivity> addSeckillActivity(Long userId, SeckillActivityDTO seckillActivity);
}
