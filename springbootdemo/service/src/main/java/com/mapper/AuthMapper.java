package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generator.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper extends BaseMapper<User> {
}
