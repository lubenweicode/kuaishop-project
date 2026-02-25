package com.constant;

/**
 * 商品常量
 */
public class ProductConstants {
    /**
     * 商品缓存key
     */
    public static final String PRODUCT_CACHE_PREFIX = "product:list:";
    public static final String PRODUCT_CACHE_KEY_PREFIX = "product:info";
    public static final String PRODUCT_CACHE_VALUE_PREFIX = "product:category:";

    /** 商品缓存时间
     * 单位：秒
     */
    public static final long CACHE_PRODUCT_LIST_EXPIRE_TIME = 10;
    public static final long CACHE_PRODUCT_ID_EXPIRE_TIME = 5;
    public static final long CACHE_PRODUCT_CATEGORY_EXPIRE_TIME = 10;

    // 商品ID不能为空错误信息
    public static final String MSG_PRODUCT_ID_NOT_NULL = "商品ID不能为空";
    public static final int CODE_PRODUCT_ID_NOT_NULL = 400;

    // 商品ID必须大于0错误信息
    public static final String MSG_PRODUCT_ID_GT_0 = "商品ID必须大于0";
    public static final int CODE_PRODUCT_ID_GT_0 = 400;

    // 商品不存在错误信息
    public static final String MSG_PRODUCT_NOT_EXIST = "商品不存在";
    public static final int CODE_PRODUCT_NOT_EXIST = 404;

}
