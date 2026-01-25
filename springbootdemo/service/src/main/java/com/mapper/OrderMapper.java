package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generator.domain.Entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
