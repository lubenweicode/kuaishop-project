package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import generator.domain.entity.Product;
import generator.domain.entity.ProductCategory;
import generator.domain.demo.Result;
import generator.domain.product.ProductDTO;
import generator.domain.product.ProductListVO;

import java.util.List;

public interface ProductService extends IService<Product> {
    Result<ProductListVO> getProducts(ProductDTO productDTO) throws JsonProcessingException;

    Result<Product> getProductById(Long id) throws JsonProcessingException;

    Result<List<ProductCategory>> getProductCategories() throws JsonProcessingException;
}
