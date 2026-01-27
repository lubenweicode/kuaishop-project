package com.controller;

import com.utils.JwtUtil;
import com.utils.UserRateLimiter;
import generator.domain.cart.CartAddItem;
import generator.domain.cart.CartItemVO;
import generator.domain.context.UserContext;
import generator.domain.demo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.service.service.CartService;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private UserRateLimiter userRateLimiter;

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 添加购物车项
     */
    @PostMapping("/items")
    public Result<Void> addCartItem(HttpServletRequest  request,@RequestBody CartAddItem cartAddItem){
        // 1. 限制用户10s内最多访问5次
        if (!userRateLimiter.allowRequest(request)){
            return Result.error(429,"请求过于频繁");
        }
        // 2. 获取用户ID
        Long userId = UserContext.getUserId();
        if (userId == null){
            return Result.error(401,"请先登录");
        }
        // 3. 调用业务逻辑
        Integer productId = cartAddItem.getProductId();
        Integer quantity = cartAddItem.getQuantity();
        return cartService.addCartItem(userId,productId,quantity);
    }

    /**
     * 获取购物车项
     */
    @GetMapping("/items")
    public Result<List<CartItemVO>> getCartItems(){
        Long userId = UserContext.getUserId();
        return cartService.getCartItems(userId);
    }

    /**
     * 修改购物车项数量
     */
    @PutMapping("/items/{itemId}")
    public Result<Void> updateCartItem(@PathVariable Integer itemId,@RequestBody CartAddItem cartAddItem){
        Long userId = UserContext.getUserId();
        return cartService.updateCartItem(userId,itemId,cartAddItem);
    }

    /**
     * 删除购物车项
     */
    @DeleteMapping("/items/{itemId}")
    public Result<Void> deleteCartItem(@PathVariable Integer itemId){
        Long userId = UserContext.getUserId();
        return cartService.deleteCartItem(userId,itemId);
    }
}
