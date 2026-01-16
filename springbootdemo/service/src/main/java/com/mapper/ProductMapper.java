package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generator.domain.Entity.Product;
import generator.domain.Entity.ProductCategory;
import generator.domain.product.ProductDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {


    List<Product> selectByCondition(ProductDTO productDTO);

    @Select("select id from product_category")
    List<String> selectCategoryIds();

    @Select("select * from product_category where id = #{categoryId}")
    ProductCategory selectCategoryById(String categoryId);
}
