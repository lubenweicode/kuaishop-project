package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.SeckillProductMapper;
import com.service.SeckillProductService;
import generator.domain.entity.SeckillProduct;
import org.springframework.stereotype.Service;

/**
* @author 1569157760
* @description 针对表【seckill_product(秒杀商品关联表)】的数据库操作Service实现
* @createDate 2026-01-29 13:31:28
*/
@Service
public class SeckillProductServiceImpl extends ServiceImpl<SeckillProductMapper, SeckillProduct>
    implements SeckillProductService {

}




