package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import generator.domain.entity.Cart;
import generator.domain.cart.CartAddItem;
import generator.domain.cart.CartItemVO;
import generator.domain.demo.Result;

import java.util.List;

public interface CartService extends IService<Cart> {
    Result<Void> addCartItem(Long userId, Integer productId, Integer quantity);

    Result<List<CartItemVO>> getCartItems(Long userId);

    Result<Void> deleteCartItem(Long userId, Integer itemId);

    Result<Void> updateCartItem(Long userId, Integer itemId, CartAddItem cartAddItem);
}