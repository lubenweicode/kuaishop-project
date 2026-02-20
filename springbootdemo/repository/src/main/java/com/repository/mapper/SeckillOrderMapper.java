package com.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generator.domain.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillOrderMapper extends BaseMapper<Orders> {
}
