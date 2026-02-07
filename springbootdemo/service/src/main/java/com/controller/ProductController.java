package com.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.ProductService;
import generator.domain.demo.Result;
import generator.domain.entity.Product;
import generator.domain.entity.ProductCategory;
import generator.domain.product.ProductListVO;
import generator.domain.product.ProductPageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }


    /**
     * 商品列表
     * @param page 默认5
     * @param size 默认12
     * @param keyword 搜索关键字
     * @param categoryId 分类ID
     * @param sortBy 排序字段 price\sales\createTime
     * @param order 排序方式 asc\desc
     * @return
     */
    @GetMapping
    public Result<ProductListVO> getProducts(@RequestParam(value = "page",required = false, defaultValue = "5")Integer page,
                                             @RequestParam(value = "size",required = false, defaultValue = "12")Integer size,
                                             @RequestParam(value = "keyword",required = false)String keyword,
                                             @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                             @RequestParam(value = "sortBy",required = false, defaultValue = "price")String sortBy,
                                             @RequestParam(value = "order",required = false, defaultValue = "asc")String order) throws JsonProcessingException {
        ProductPageDTO productDTO = new ProductPageDTO();
        productDTO.setPage(page);
        productDTO.setSize(size);
        productDTO.setKeyword(keyword);
        productDTO.setCategoryId(categoryId);
        productDTO.setSortBy(sortBy);
        productDTO.setOrder(order);
        return productService.getProducts(productDTO);
    }

    /**
     * 商品详情
     * @param id 商品ID
     * @return
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) throws JsonProcessingException {
        return productService.getProductById(id);
    }

    /**
     * 商品分类列表
     * @return
     */
    @GetMapping("/categories")
    public Result<List<ProductCategory>> getProductCategories() throws JsonProcessingException {
        return productService.getProductCategories();
    }
}
