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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final static String CART_LIST_KEY_PREFIX = "cart:list:";
    private static final long CART_CACHE_EXPIRE_TIME = 30; // 购物车缓存过期时间（分钟）
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
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        if (productId == null || productId <= 0) {
            return Result.error(400, "商品ID无效");
        }

        if (quantity == null || quantity <= 0) {
            return Result.error(400, "商品数量无效");
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
                return Result.error(500, "添加购物车失败");
            } else {
                return Result.success();
            }
        }
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(Long.valueOf(productId));
        cart.setQuantity(quantity);
        boolean addCartItem = this.save(cart);
        if (!addCartItem) {
            return Result.error(500, "添加购物车失败");
        } else {
            return Result.success();
        }
    }

    @Override
    public Result<List<CartItemVO>> getCartItems(Long userId) {
        if (userId == null) {
            return Result.error(401, "请先登录");
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
        return Result.success(cartItemVOS);
    }

    @Override
    public Result<Void> deleteCartItem(Long userId, Integer itemId) {

        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        if (itemId == null || itemId <= 0) {
            return Result.error(400, "商品ID无效");
        }

        LambdaQueryWrapper<Cart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Cart::getUserId, userId);
        lambdaQueryWrapper.eq(Cart::getProductId, itemId);
        boolean deleteCartItem = this.remove(lambdaQueryWrapper);
        if (!deleteCartItem) {
            return Result.error(500, "删除购物车项失败");
        } else {
            redisJsonUtil.deleteKey(CART_LIST_KEY_PREFIX + userId);
            return Result.success();
        }
    }

    @Override
    public Result<Void> updateCartItem(Long userId, Integer itemId, CartAddItem cartAddItem) {

        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        if (itemId == null || itemId <= 0) {
            return Result.error(400, "商品ID无效");
        }

        LambdaQueryWrapper<Cart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Cart::getUserId, userId);
        lambdaQueryWrapper.eq(Cart::getProductId, itemId);
        Cart cart = this.getOne(lambdaQueryWrapper);
        if (cart == null) {
            return Result.error(404, "购物车项不存在");
        }
        cart.setQuantity(cartAddItem.getQuantity());
        boolean updateCartItem = this.update(cart, lambdaQueryWrapper);
        if (!updateCartItem) {
            return Result.error(500, "更新购物车项失败");
        } else {
            redisJsonUtil.deleteKey(CART_LIST_KEY_PREFIX + userId);
            return Result.success();
        }
    }
}
