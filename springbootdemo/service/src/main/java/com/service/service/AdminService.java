package com.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import generator.domain.demo.Result;
import generator.domain.entity.User;
import generator.domain.product.ProductDTO;
import generator.domain.product.ProductVO;

public interface AdminService extends IService<User> {
    Result<ProductVO> addProducts(Long userId,ProductDTO productDTO);
}
