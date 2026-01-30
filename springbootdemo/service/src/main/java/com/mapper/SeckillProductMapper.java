package com.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generator.domain.entity.SeckillProduct;
import generator.domain.seckill.SeckillProductVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 1569157760
* @description 针对表【seckill_product(秒杀商品关联表)】的数据库操作Mapper
* @createDate 2026-01-29 13:31:28
* @Entity generator.domain.entity.SeckillProduct
*/
@Mapper
public interface SeckillProductMapper extends BaseMapper<SeckillProduct> {


    @Insert("INSERT INTO seckill_product(" +
            "product_id, product_name, seckill_price, original_price, total_stock, limit_per_user" +
            ") VALUES (" +
            "#{productId}, #{productName}, #{seckillPrice}, #{originalPrice}, #{totalStock}, #{limitPerUser}" +
            ")")
    void insert(SeckillProductVO seckillProductVO);
    

    @Select("SELECT COUNT(*) FROM seckill_product")
    long selectCount(LambdaQueryWrapper<SeckillProductVO> seckillProductQueryWrapper);


    boolean insertBatch(List<SeckillProduct> seckillProductList);
}




