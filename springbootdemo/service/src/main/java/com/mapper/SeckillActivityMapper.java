package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generator.domain.entity.SeckillActivity;
import generator.domain.seckill.SeckillActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 1569157760
* @description 针对表【seckill_activity(秒杀活动表)】的数据库操作Mapper
* @createDate 2026-01-29 13:31:28
* @Entity generator.domain.entity.SeckillActivity
*/
@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {

    @Select("SELECT * FROM seckill_activity WHERE status = #{status}")
    List<SeckillActivity> getSeckillActivities(Integer status);
}




