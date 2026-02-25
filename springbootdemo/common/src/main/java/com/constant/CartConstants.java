package com.constant;

/**
 * 购物车常量类
 */
public class CartConstants {

    // 登录错误信息
    public static final String MSG_LOGIN_ERROR = "请先登录";
    public static final int CODE_LOGIN_ERROR = 401;

    // 商品ID无效错误信息
    public static final String MSG_PRODUCT_ID_INVALID = "商品ID无效";
    public static final int CODE_PRODUCT_ID_INVALID = 400;

    // 商品数量无效错误信息
    public static final String MSG_QUANTITY_INVALID = "商品数量无效";
    public static final int CODE_QUANTITY_INVALID = 400;

    // 添加购物车成功信息
    public static final String MSG_ADD_CART_SUCCESS = "添加购物车成功";
    public static final int CODE_ADD_CART_SUCCESS = 200;

    // 添加购物车失败信息
    public static final String MSG_ADD_CART_FAIL = "添加购物车失败";
    public static final int CODE_ADD_CART_FAIL = 500;

    // 获取购物车列表成功信息
    public static final String MSG_GET_CART_LIST_SUCCESS = "获取购物车列表成功";
    public static final int CODE_GET_CART_LIST_SUCCESS = 200;

    // 获取购物车列表失败信息
    public static final String MSG_GET_CART_LIST_FAIL = "获取购物车列表失败";
    public static final int CODE_GET_CART_LIST_FAIL = 500;

    // 删除购物车项成功信息
    public static final String MSG_DELETE_CART_ITEM_SUCCESS = "删除购物车项成功";
    public static final int CODE_DELETE_CART_ITEM_SUCCESS = 200;

    // 删除购物车项失败信息
    public static final String MSG_DELETE_CART_ITEM_FAIL = "删除购物车项失败";
    public static final int CODE_DELETE_CART_ITEM_FAIL = 500;

    // 更新购物车项成功信息
    public static final String MSG_UPDATE_CART_ITEM_SUCCESS = "更新购物车项成功";
    public static final int CODE_UPDATE_CART_ITEM_SUCCESS = 200;

    // 删除购物车项失败信息
    public static final String MSG_UPDATE_CART_ITEM_FAIL = "更新购物车项失败";
    public static final int CODE_UPDATE_CART_ITEM_FAIL = 500;

    // 购物车项不存在错误信息
    public static final String MSG_CART_ITEM_NOT_FOUND = "购物车项不存在";
    public static final int CODE_CART_ITEM_NOT_FOUND = 404;

    private CartConstants() {
    }


    // 购物车列表缓存键前缀
    public final static String CART_LIST_KEY_PREFIX = "cart:list:";

    // 购物车列表缓存过期时间（分钟）
    public final static long CART_CACHE_EXPIRE_TIME = 30;
}
