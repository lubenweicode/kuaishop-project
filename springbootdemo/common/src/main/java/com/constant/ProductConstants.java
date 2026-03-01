package com.constant;

/**
 * 商品模块常量定义
 *
 * <p>包含商品缓存配置、参数校验错误码及提示信息
 * 错误码采用HTTP状态码标准：
 * <ul>
 *   <li>400 - 客户端错误：请求参数校验失败</li>
 *   <li>404 - 客户端错误：请求的资源不存在</li>
 * </ul>
 *
 * @author your-name
 * @since 1.0
 */
public class ProductConstants {

    /**
     * ==================== Redis缓存配置 ====================
     * 商品相关数据的缓存key前缀及过期时间设置
     */

    /* ---------- 缓存key前缀 ---------- */
    /** 商品列表缓存前缀：完整key = PRODUCT_CACHE_PREFIX + 分页参数/查询条件 */
    public static final String PRODUCT_CACHE_PREFIX = "product:list:";

    /** 商品详情缓存前缀：完整key = PRODUCT_CACHE_KEY_PREFIX + ":" + productId */
    public static final String PRODUCT_CACHE_KEY_PREFIX = "product:info";

    /** 商品分类缓存前缀：完整key = PRODUCT_CACHE_VALUE_PREFIX + categoryId */
    public static final String PRODUCT_CACHE_VALUE_PREFIX = "product:category:";

    /* ---------- 缓存过期时间 ---------- */
    /**
     * 商品列表缓存过期时间（秒）：10秒
     * 适用于首页推荐、分类列表等动态性较强的场景
     */
    public static final long CACHE_PRODUCT_LIST_EXPIRE_TIME = 10;

    /**
     * 商品详情缓存过期时间（秒）：5秒
     * 商品详情更新频率较低，但需保证秒杀等场景的实时性，故设置较短时间
     */
    public static final long CACHE_PRODUCT_ID_EXPIRE_TIME = 5;

    /**
     * 商品分类缓存过期时间（秒）：10秒
     * 分类信息相对稳定，可适当延长缓存时间
     */
    public static final long CACHE_PRODUCT_CATEGORY_EXPIRE_TIME = 10;

    /**
     * ==================== 商品参数校验 ====================
     * 商品相关接口的请求参数校验错误码及提示
     */

    /** 400 商品ID不能为空：查询/操作商品时必传 */
    public static final String MSG_PRODUCT_ID_NOT_NULL = "商品ID不能为空";
    public static final int CODE_PRODUCT_ID_NOT_NULL = 400;

    /** 400 商品ID必须大于0：商品ID从1开始自增 */
    public static final String MSG_PRODUCT_ID_GT_0 = "商品ID必须大于0";
    public static final int CODE_PRODUCT_ID_GT_0 = 400;

    /** 404 商品不存在：查询的商品ID在数据库中不存在 */
    public static final String MSG_PRODUCT_NOT_EXIST = "商品不存在";
    public static final int CODE_PRODUCT_NOT_EXIST = 404;

    /**
     * 私有构造函数，防止实例化
     */
    private ProductConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}