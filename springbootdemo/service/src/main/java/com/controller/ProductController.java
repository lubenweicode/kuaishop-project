package com.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.ProductService;
import generator.domain.entity.Product;
import generator.domain.entity.ProductCategory;
import generator.domain.demo.Result;
import generator.domain.product.ProductDTO;
import generator.domain.product.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 商品列表
     * @param productDTO
     * @return
     */
    @GetMapping
    public Result<ProductListVO> getProducts(@RequestBody ProductDTO productDTO) throws JsonProcessingException {
        return productService.getProducts(productDTO);
    }

    /**
     * 商品详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) throws JsonProcessingException {
        return productService.getProductById(id);
    }

    @GetMapping("/categories")
    public Result<List<ProductCategory>> getProductCategories() throws JsonProcessingException {
        return productService.getProductCategories();
    }
}
