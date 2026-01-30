package com.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import generator.domain.entity.Product;
import generator.domain.entity.User;
import generator.domain.order.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper extends BaseMapper<User> {

    @Insert("insert into product (name, description, detail, price, original_price, stock, category_id, images, specifications, status)" +
            " values (#{name}, #{description}, #{detail}, #{price}, #{originalPrice}, #{stock}, #{categoryId}, #{images}, #{specifications}, #{status})")
    boolean addProducts(Product product);

    @Insert("insert into orders (order_no, status, create_time)" +
            " values (#{orderNo}, #{status}, #{createTime})")
    List<Order> selectList(IPage<Order> ipage, LambdaQueryWrapper<Order> queryWrapper);
}
