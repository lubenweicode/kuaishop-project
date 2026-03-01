package com.constant;

/**
 * 购物车模块常量定义
 *
 * <p>包含购物车业务相关的错误码、提示信息及缓存配置
 * 错误码采用HTTP状态码标准，便于RESTful API设计：
 * <ul>
 *   <li>2xx - 成功：操作正常完成</li>
 *   <li>4xx - 客户端错误：请求参数问题、权限问题等</li>
 *   <li>5xx - 服务端错误：系统内部异常</li>
 * </ul>
 *
 * @author your-name
 * @since 1.0
 */
public class CartConstants {

    /**
     * ==================== 客户端错误 (4xx) ====================
     * 由用户请求问题导致的错误，前端可根据具体code给出相应提示
     */

    /** 401 未登录：用户需要先登录才能操作购物车 */
    public static final String MSG_LOGIN_ERROR = "请先登录";
    public static final int CODE_LOGIN_ERROR = 401;

    /** 400 商品ID无效：请求中传递的商品ID为空、不存在或格式错误 */
    public static final String MSG_PRODUCT_ID_INVALID = "商品ID无效";
    public static final int CODE_PRODUCT_ID_INVALID = 400;

    /** 400 商品数量无效：购买数量必须大于0（具体上限由商品库存决定） */
    public static final String MSG_QUANTITY_INVALID = "商品数量无效";
    public static final int CODE_QUANTITY_INVALID = 400;

    /** 404 购物车项不存在：尝试查询、更新或删除不存在的购物车记录 */
    public static final String MSG_CART_ITEM_NOT_FOUND = "购物车项不存在";
    public static final int CODE_CART_ITEM_NOT_FOUND = 404;

    /**
     * ==================== 服务端错误 (5xx) ====================
     * 由服务器内部异常导致，前端应提示“系统繁忙，请稍后重试”
     */

    /** 500 添加购物车失败：可能原因包括数据库异常、Redis连接失败等 */
    public static final String MSG_ADD_CART_FAIL = "添加购物车失败";
    public static final int CODE_ADD_CART_FAIL = 500;

    /** 500 获取购物车列表失败：查询购物车时发生系统异常 */
    public static final String MSG_GET_CART_LIST_FAIL = "获取购物车列表失败";
    public static final int CODE_GET_CART_LIST_FAIL = 500;

    /** 500 删除购物车项失败：删除操作发生系统异常 */
    public static final String MSG_DELETE_CART_ITEM_FAIL = "删除购物车项失败";
    public static final int CODE_DELETE_CART_ITEM_FAIL = 500;

    /** 500 更新购物车项失败：更新操作发生系统异常 */
    public static final String MSG_UPDATE_CART_ITEM_FAIL = "更新购物车项失败";
    public static final int CODE_UPDATE_CART_ITEM_FAIL = 500;

    /**
     * ==================== 成功提示 (2xx) ====================
     * 操作成功完成，data字段包含具体返回数据
     */

    /** 200 添加购物车成功 */
    public static final String MSG_ADD_CART_SUCCESS = "添加购物车成功";
    public static final int CODE_ADD_CART_SUCCESS = 200;

    /** 200 获取购物车列表成功 */
    public static final String MSG_GET_CART_LIST_SUCCESS = "获取购物车列表成功";
    public static final int CODE_GET_CART_LIST_SUCCESS = 200;

    /** 200 删除购物车项成功 */
    public static final String MSG_DELETE_CART_ITEM_SUCCESS = "删除购物车项成功";
    public static final int CODE_DELETE_CART_ITEM_SUCCESS = 200;

    /** 200 更新购物车项成功 */
    public static final String MSG_UPDATE_CART_ITEM_SUCCESS = "更新购物车项成功";
    public static final int CODE_UPDATE_CART_ITEM_SUCCESS = 200;

    /**
     * ==================== Redis缓存配置 ====================
     * 购物车数据存储在Redis中的相关配置
     */

    /** 购物车列表缓存键前缀：完整key = CART_LIST_KEY_PREFIX + userId */
    public final static String CART_LIST_KEY_PREFIX = "cart:list:";

    /**
     * 购物车列表缓存过期时间（分钟）
     * 设置为30分钟，平衡内存占用和用户体验：
     * - 太短：用户频繁操作需重复加载
     * - 太长：占用Redis内存，数据可能过时
     */
    public final static long CART_CACHE_EXPIRE_TIME = 30;

    /**
     * 私有构造函数，防止实例化
     */
    private CartConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}