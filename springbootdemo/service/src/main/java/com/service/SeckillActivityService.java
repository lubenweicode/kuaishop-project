package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import domain.entity.SeckillActivity;
import domain.order.OrderVO;
import domain.response.Result;
import domain.seckill.SeckillActivityVO;
import domain.seckill.SeckillOrderAddDTO;

import java.util.List;

/**
 * @author 1569157760
 * @description 针对表【seckill_activity(秒杀活动表)】的数据库操作Service
 * @createDate 2026-01-29 13:31:28
 */
public interface SeckillActivityService extends IService<SeckillActivity> {

    Result<List<SeckillActivityVO>> getSeckillActivities(Integer status);

    Result<OrderVO> createSeckillOrder(Long userId, SeckillOrderAddDTO seckillOrderAddDTO);
}
