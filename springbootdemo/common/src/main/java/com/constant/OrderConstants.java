package com.constant;

/**
 * 订单模块常量定义
 *
 * <p>包含订单状态、分布式锁配置、错误码及提示信息等
 * 错误码范围：10001-10099 为订单模块预留
 *
 * @author your-name
 * @since 1.0
 */
public final class OrderConstants {

    /**
     * ==================== 订单状态（0-9） ====================
     * 订单生命周期状态定义，数值越小越靠近初始状态
     */
    public static final Integer ORDER_STATUS_PENDING_PAYMENT = 0;  // 待付款：订单已创建，等待用户支付
    public static final Integer ORDER_STATUS_PENDING_DELIVERY = 1;  // 待发货：已付款，等待商家发货
    public static final Integer ORDER_STATUS_PENDING_RECEIPT = 2;   // 待收货：已发货，等待用户确认收货
    public static final Integer ORDER_STATUS_COMPLETED = 3;         // 已完成：交易成功完成
    public static final Integer ORDER_STATUS_CANCELLED = 4;         // 已取消：订单取消或超时关闭

    /**
     * ==================== 分布式锁配置 ====================
     * Redis分布式锁相关配置，单位：毫秒
     */
    /** 订单创建锁的Redis key前缀，完整key = prefix + userId + ":" + productId */
    public static final String ORDER_LOCK_PREFIX = "order:lock:";

    /** 锁自动释放时间（毫秒）：30秒，需大于业务处理最大耗时 */
    public static final Long LOCK_EXPIRE_TIME = 30000L;

    /** 获取锁的最大等待时间（毫秒）：5秒，超过则放弃获取锁 */
    public static final Long LOCK_WAIT_TIME = 5000L;

    /** 获取锁的最大重试次数：5次，防止死循环 */
    public static final Integer LOCK_MAX_RETRY = 5;

    /**
     * ==================== 订单编号规则 ====================
     * 订单号生成规则：前缀 + 时间戳 + 随机数
     */
    /** 普通订单前缀：QS（QiangShou/抢手） */
    public static final String ORDER_NO_PREFIX = "QS";

    /** 秒杀订单前缀：SK（Seckill） */
    public static final String SEC_KILL_NO_PREFIX = "SK";

    /**
     * ==================== 业务错误码及提示 ====================
     * 错误码规则：1开头表示订单模块，后三位为具体错误码
     * 成功类使用偶数，失败类使用奇数，便于快速识别
     */

    /* ---------- 通用错误 (10001-10019) ---------- */
    public static final int ERROR_CODE_LOGIN_REQUIRED = 10001;
    public static final String MSG_LOGIN_REQUIRED = "请先登录";

    public static final int ERROR_CODE_SELECT_PRODUCT = 10002;
    public static final String MSG_SELECT_PRODUCT = "请选择要购买的商品";

    public static final int ERROR_CODE_USER_ID_REQUIRED = 10010;
    public static final String MSG_USER_ID_REQUIRED = "用户ID不能为空";

    public static final int ERROR_CODE_PAGE_NUM_INVALID = 10011;
    public static final String MSG_PAGE_NUM_INVALID = "页码必须大于等于1";

    public static final int ERROR_CODE_PAGE_SIZE_INVALID = 10012;
    public static final String MSG_PAGE_SIZE_INVALID = "每页条数必须大于等于1";

    /* ---------- 商品相关错误 (10020-10039) ---------- */
    public static final int ERROR_CODE_PRODUCT_NOT_EXIST = 10020;
    public static final String MSG_PRODUCT_NOT_EXIST = "商品不存在或已下架";

    public static final int ERROR_CODE_PRODUCT_QUANTITY_INVALID = 10021;
    public static final String MSG_PRODUCT_QUANTITY_INVALID = "商品购买数量必须大于0";

    public static final int ERROR_CODE_STOCK_INSUFFICIENT = 10022;
    public static final String MSG_STOCK_INSUFFICIENT = "商品库存不足";

    public static final int ERROR_CODE_STOCK_DEDUCT_FAILED = 10023;
    public static final String MSG_STOCK_DEDUCT_FAILED = "库存扣减失败，请重试";

    /* ---------- 订单创建错误 (10040-10059) ---------- */
    public static final int ERROR_CODE_DUPLICATE_ORDER = 10040;
    public static final String MSG_DUPLICATE_ORDER = "请勿重复提交订单";

    public static final int ERROR_CODE_ORDER_CREATE_FAILED = 10041;
    public static final String MSG_ORDER_CREATE_FAILED = "订单创建失败，请稍后重试";

    public static final int ERROR_CODE_ORDER_CREATE_EXCEPTION = 10042;
    public static final String MSG_ORDER_CREATE_EXCEPTION = "订单创建异常，请联系客服";

    /* ---------- 订单操作错误 (10060-10079) ---------- */
    public static final int ERROR_CODE_ORDER_NO_REQUIRED = 10060;
    public static final String MSG_ORDER_NO_REQUIRED = "订单号不能为空";

    public static final int ERROR_CODE_ORDER_NOT_EXIST = 10061;
    public static final String MSG_ORDER_NOT_EXIST = "订单不存在";

    public static final int ERROR_CODE_ORDER_STATUS_ERROR = 10062;
    public static final String MSG_ORDER_STATUS_ERROR = "当前订单状态不允许此操作";

    public static final int ERROR_CODE_ORDER_CANCEL_FAILED = 10063;
    public static final String MSG_ORDER_CANCEL_FAILED = "订单取消失败";

    public static final int ERROR_CODE_ORDER_DELETE_FAILED = 10064;
    public static final String MSG_ORDER_DELETE_FAILED = "订单删除失败";

    public static final int ERROR_CODE_ONLY_PENDING_PAYMENT_CANCEL = 10065;
    public static final String MSG_ONLY_PENDING_PAYMENT_CANCEL = "仅待付款订单可取消";

    public static final int ERROR_CODE_ONLY_FINISHED_DELETABLE = 10066;
    public static final String MSG_ONLY_FINISHED_DELETABLE = "仅已完成或已取消的订单可删除";

    /* ---------- 支付相关错误 (10080-10099) ---------- */
    public static final int ERROR_CODE_PAY_TYPE_NOT_NULL = 10080;
    public static final String MSG_PAY_TYPE_NOT_NULL = "请选择支付方式";

    public static final int ERROR_CODE_PAY_TYPE_ERROR = 10081;
    public static final String MSG_PAY_TYPE_ERROR = "不支持的支付方式";

    /* ---------- 成功提示 ---------- */
    public static final int SUCCESS_CODE_ORDER_CANCEL = 10100;
    public static final String SUCCESS_MSG_ORDER_CANCEL = "订单取消成功";

    public static final int SUCCESS_CODE_ORDER_DELETE = 10101;
    public static final String SUCCESS_MSG_ORDER_DELETE = "订单删除成功";

    public static final String SUCCESS_MSG_PAY_NOTIFY = "支付成功";

    /**
     * 私有构造函数，防止实例化
     */
    private OrderConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}