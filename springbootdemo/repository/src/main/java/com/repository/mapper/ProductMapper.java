package com.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import domain.entity.Product;
import domain.entity.ProductCategory;
import domain.product.ProductPageDTO;
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

    /**
     * 回滚库存
     *
     * @param key   商品ID
     * @param value 购买数量
     */
    @Select("update product set stock = stock + #{value} where id = #{key}")
    void rollbackStock(Long key, Integer value);
}
