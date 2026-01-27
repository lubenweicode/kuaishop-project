package com.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.AdminMapper;
import com.service.service.AdminService;
import com.utils.IdentityUtil;
import generator.domain.demo.Result;
import generator.domain.entity.Product;
import generator.domain.entity.User;
import generator.domain.product.ProductDTO;
import generator.domain.product.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminServiceImpl extends ServiceImpl<AdminMapper, User> implements AdminService {

    private final IdentityUtil identityUtil;
    private final AdminMapper adminMapper;

    public AdminServiceImpl(IdentityUtil identityUtil, AdminMapper adminMapper) {
        this.identityUtil = identityUtil;
        this.adminMapper = adminMapper;
    }

    @Override
    public Result<ProductVO> addProducts(Long userId,ProductDTO productDTO) {

        if (!identityUtil.confirmIdentity(userId)){
            log.error("用户 {} 无权限", userId);
            return Result.error(401,"用户无权限");
        }

        if(productDTO.getName() == null){
            log.error("用户 {} 商品名称不能为空", userId);
            return Result.error(400,"商品名称不能为空");
        }

        if(productDTO.getDescription() == null){
            log.error("用户 {} 商品描述不能为空", userId);
            return Result.error(400,"商品描述不能为空");
        }

        if(productDTO.getPrice() == null){
            log.error("用户 {} 商品价格不能为空", userId);
            return Result.error(400,"商品价格不能为空");
        }

        if(productDTO.getStock() == null){
            log.error("用户 {} 商品库存不能为空", userId);
            return Result.error(400,"商品库存不能为空");
        }

        if(productDTO.getCategoryId() == null){
            log.error("用户 {} 商品分类不能为空", userId);
            return Result.error(400,"商品分类不能为空");
        }

        if(productDTO.getImages() == null){
            log.error("用户 {} 商品图片不能为空", userId);
            return Result.error(400,"商品图片不能为空");
        }

        if(productDTO.getSpecifications() == null){
            log.error("用户 {} 商品规格不能为空", userId);
            return Result.error(400,"商品规格不能为空");
        }

        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);

        boolean result = adminMapper.addProducts(product);

        if(result){
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            return Result.success(productVO);
        }else{
            return Result.error(500,"添加商品失败");
        }
    }
}
