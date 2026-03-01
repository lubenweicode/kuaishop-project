package com.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import domain.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
