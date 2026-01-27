package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generator.domain.entity.Product;
import generator.domain.entity.User;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface AdminMapper extends BaseMapper<User> {

    @Insert("insert into product (name, description, detail, price, original_price, stock, category_id, images, specifications, status)" +
            " values (#{name}, #{description}, #{detail}, #{price}, #{originalPrice}, #{stock}, #{categoryId}, #{images}, #{specifications}, #{status})")
    boolean addProducts(Product product);
}
