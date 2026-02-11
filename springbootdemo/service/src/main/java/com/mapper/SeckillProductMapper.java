package mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import generator.domain.entity.SeckillProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 1569157760
* @description 针对表【seckill_product(秒杀商品关联表)】的数据库操作Mapper
* @createDate 2026-01-29 13:31:28
* @Entity generator.domain.entity.SeckillProduct
*/
@Mapper
public interface SeckillProductMapper extends BaseMapper<SeckillProduct> {



    boolean insertBatch(List<SeckillProduct> seckillProductList);
}




