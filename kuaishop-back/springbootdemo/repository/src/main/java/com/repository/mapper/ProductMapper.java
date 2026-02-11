package com.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generator.domain.entity.Product;
import generator.domain.entity.ProductCategory;
import generator.domain.product.ProductPageDTO;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {


    List<Product> selectByCondition(ProductPageDTO productDTO);

    @Select("select id from product_category")
    List<String> selectCategoryIds();

    @Select("select * from product_category where id = #{categoryId}")
    ProductCategory selectCategoryById(String categoryId);

    @Select("select * from product where id = #{productId}")
    Product getProductById(@NotNull(message = "[商品ID]不能为空") Long productId);


    Integer updateStock(Long productId, Integer quantity);
}
