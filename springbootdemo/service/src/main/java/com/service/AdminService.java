package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import generator.domain.entity.SeckillActivity;
import generator.domain.entity.User;
import generator.domain.order.OrderPageVO;
import generator.domain.product.ProductDTO;
import generator.domain.product.ProductVO;
import generator.domain.response.Result;
import generator.domain.seckill.SeckillActivityDTO;
import generator.domain.statistics.Statistics;

public interface AdminService extends IService<User> {
    Result<ProductVO> addProducts(Long userId, ProductDTO productDTO);

    Result<OrderPageVO> getOrderList(Long userId, Integer page, Integer size, String orderNo, String startTime, String endTime, Integer status);

    Result<String> shipOrder(Long userId, String orderNo, String logisticsCompany, String logisticsNo);

    Result<Statistics> getSalesStatistics(Long userId, String type, String startTime, String endTime);

    Result<SeckillActivity> addSeckillActivity(Long userId, SeckillActivityDTO seckillActivity);
}
