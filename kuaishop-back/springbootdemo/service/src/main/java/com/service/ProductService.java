package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import generator.domain.entity.Product;
import generator.domain.entity.ProductCategory;
import generator.domain.product.ProductListVO;
import generator.domain.product.ProductPageDTO;
import generator.domain.response.Result;

import java.util.List;

public interface ProductService extends IService<Product> {

    /**
     * 商品列表
     * 所有用户都可以查看
     *
     * @param productDTO
     * @return
     */
    Result<ProductListVO> getProducts(ProductPageDTO productDTO) throws JsonProcessingException;

    /**
     * 商品详情
     * 所有用户都可以查看
     *
     * @param id
     * @return
     */
    Result<Product> getProductById(Long id) throws JsonProcessingException;

    /**
     * 商品分类
     * 所有用户都可以查看
     *
     * @return
     */
    Result<List<ProductCategory>> getProductCategories() throws JsonProcessingException;


}
