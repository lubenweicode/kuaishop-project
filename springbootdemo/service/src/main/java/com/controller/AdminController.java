package com.controller;

import com.service.service.AdminService;
import com.utils.IdentityUtil;
import generator.domain.context.UserContext;
import generator.domain.demo.Result;
import generator.domain.product.ProductDTO;
import generator.domain.product.ProductVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(IdentityUtil identityUtil, AdminService adminService) {
        this.adminService = adminService;
    }


    /**
     * 添加商品
     * @param productDTO
     * @return
     */
    @PostMapping("/products")
    public Result<ProductVO> addProducts(@RequestBody ProductDTO productDTO) {
        Long userId = UserContext .getUserId();
        return adminService.addProducts(userId,productDTO);
    }
}
