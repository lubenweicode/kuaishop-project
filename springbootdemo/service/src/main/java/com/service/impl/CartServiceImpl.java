package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.repository.mapper.CartMapper;
import com.repository.mapper.ProductMapper;
import com.service.CartService;
import com.utils.RedisJsonUtil;
import generator.domain.cart.CartAddItem;
import generator.domain.cart.CartItemVO;
import generator.domain.entity.Cart;
import generator.domain.entity.Product;
import generator.domain.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.constant.CartConstants.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final StringRedisTemplate redisTemplate;
    private final RedisJsonUtil redisJsonUtil;

    public CartServiceImpl(CartMapper cartMapper, ProductMapper productMapper, StringRedisTemplate redisTemplate, RedisJsonUtil redisJsonUtil) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.redisTemplate = redisTemplate;
        this.redisJsonUtil = redisJsonUtil;
    }

    @Override
    public Result<Void> addCartItem(Long userId, Integer productId, Integer quantity) {
        // 1. 参数校验
        // 1.1 用户ID
        if (userId == null) {
            return Result.error(CODE_LOGIN_ERROR , MSG_LOGIN_ERROR);
        }
        // 1.2 商品ID
        if (productId == null || productId <= 0) {
            return Result.error(CODE_PRODUCT_ID_INVALID, MSG_PRODUCT_ID_INVALID);
        }
        // 1.3 商品数量
        if (quantity == null || quantity <= 0) {
            return Result.error(CODE_QUANTITY_INVALID, MSG_QUANTITY_INVALID);
        }

        LambdaQueryWrapper<Cart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Cart::getUserId, userId);
        lambdaQueryWrapper.eq(Cart::getProductId, productId);

        boolean existCartItem = this.exists(lambdaQueryWrapper);
        if (existCartItem) {
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("product_id", productId);
            Integer oldQuantity = this.getOne(queryWrapper).getQuantity();
            Cart cart = new Cart();
            cart.setQuantity(oldQuantity + quantity);
            boolean updateCartItem = this.update(cart, queryWrapper);
            if (!updateCartItem) {
                return Result.error(CODE_ADD_CART_FAIL, MSG_ADD_CART_FAIL);
            } else {
                return Result.success(CODE_ADD_CART_SUCCESS, MSG_ADD_CART_SUCCESS);
            }
        }
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(Long.valueOf(productId));
        cart.setQuantity(quantity);
        boolean addCartItem = this.save(cart);
        if (!addCartItem) {
            return Result.error(CODE_ADD_CART_FAIL, MSG_ADD_CART_FAIL);
        } else {
            return Result.success(CODE_ADD_CART_SUCCESS, MSG_ADD_CART_SUCCESS);
        }
    }

    @Override
    public Result<List<CartItemVO>> getCartItems(Long userId) {
        if (userId == null) {
            return Result.error(CODE_LOGIN_ERROR, MSG_LOGIN_ERROR);
        }

        // 1. 构建 Redis 缓存键
        String cacheKey = CART_LIST_KEY_PREFIX + userId;

        // 2. 优先从 Redis 获取缓存数据
        try {
            Optional<List<CartItemVO>> cachedCartItems = redisJsonUtil.getBeanList(
                    cacheKey,
                    new TypeReference<List<CartItemVO>>() {
                    }
            );

            if (cachedCartItems.isPresent() && !cachedCartItems.get().isEmpty()) {
                log.info("从Redis缓存中获取购物车数据成功，用户ID: {}, 缓存键: {}", userId, cacheKey);
                return Result.success(cachedCartItems.get());
            }
        } catch (Exception e) {
            log.error("从Redis获取购物车数据异常，用户ID: {}, 缓存键: {}, 异常: {}", userId, cacheKey, e.getMessage(), e);
        }

        // 3. Redis 缓存未命中，从数据库查询
        LambdaQueryWrapper<Cart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Cart::getUserId, userId);

        List<Cart> carts = this.list(lambdaQueryWrapper);

        // 4. 转换为 CartItemVO 列表
        List<CartItemVO> cartItemVOS = carts.stream().map(cart -> {
            CartItemVO cartItemVO = new CartItemVO();
            cartItemVO.setProductId(cart.getProductId());
            cartItemVO.setQuantity(cart.getQuantity());

            Product product = productMapper.getProductById(cart.getProductId());
            if (product != null) {
                BeanUtils.copyProperties(product, cartItemVO);
            }
            return cartItemVO;
        }).toList();

        // 5. 将查询结果存入 Redis 缓存
        try {
            boolean cacheSuccess = redisJsonUtil.setBean(
                    cacheKey,
                    cartItemVOS,
                    CART_CACHE_EXPIRE_TIME,
                    TimeUnit.MINUTES
            );
            if (cacheSuccess) {
                log.info("购物车数据已存入Redis缓存，用户ID: {}, 缓存键: {}, 过期时间: {}分钟",
                        userId, cacheKey, CART_CACHE_EXPIRE_TIME);
            }
        } catch (Exception e) {
            log.error("将购物车数据存入Redis缓存失败，用户ID: {}, 缓存键: {}, 异常: {}",
                    userId, cacheKey, e.getMessage(), e);
        }

        log.info("从数据库中获取购物车数据成功，用户ID: {}, 购物车项数量: {}", userId, cartItemVOS.size());
        return Result.success(CODE_GET_CART_LIST_SUCCESS,MSG_GET_CART_LIST_SUCCESS,cartItemVOS);
    }

    @Override
    public Result<Void> deleteCartItem(Long userId, Integer itemId) {

        if (userId == null) {
            return Result.error(CODE_LOGIN_ERROR, MSG_LOGIN_ERROR);
        }

        if (itemId == null || itemId <= 0) {
            return Result.error(CODE_PRODUCT_ID_INVALID, MSG_PRODUCT_ID_INVALID);
        }

        LambdaQueryWrapper<Cart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Cart::getUserId, userId);
        lambdaQueryWrapper.eq(Cart::getProductId, itemId);
        boolean deleteCartItem = this.remove(lambdaQueryWrapper);
        if (!deleteCartItem) {
            return Result.error(CODE_DELETE_CART_ITEM_FAIL, MSG_DELETE_CART_ITEM_FAIL);
        } else {
            redisJsonUtil.deleteKey(CART_LIST_KEY_PREFIX + userId);
            return Result.success(CODE_DELETE_CART_ITEM_SUCCESS, MSG_DELETE_CART_ITEM_SUCCESS);
        }
    }

    @Override
    public Result<Void> updateCartItem(Long userId, Integer itemId, CartAddItem cartAddItem) {

        if (userId == null) {
            return Result.error(CODE_LOGIN_ERROR, MSG_LOGIN_ERROR);
        }

        if (itemId == null || itemId <= 0) {
            return Result.error(CODE_PRODUCT_ID_INVALID, MSG_PRODUCT_ID_INVALID);
        }

        LambdaQueryWrapper<Cart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Cart::getUserId, userId);
        lambdaQueryWrapper.eq(Cart::getProductId, itemId);
        Cart cart = this.getOne(lambdaQueryWrapper);
        if (cart == null) {
            return Result.error(CODE_CART_ITEM_NOT_FOUND, MSG_CART_ITEM_NOT_FOUND);
        }
        cart.setQuantity(cartAddItem.getQuantity());
        boolean updateCartItem = this.update(cart, lambdaQueryWrapper);
        if (!updateCartItem) {
            return Result.error(CODE_UPDATE_CART_ITEM_FAIL, MSG_UPDATE_CART_ITEM_FAIL);
        } else {
            redisJsonUtil.deleteKey(CART_LIST_KEY_PREFIX + userId);
            return Result.success(CODE_UPDATE_CART_ITEM_SUCCESS, MSG_UPDATE_CART_ITEM_SUCCESS);
        }
    }
}
