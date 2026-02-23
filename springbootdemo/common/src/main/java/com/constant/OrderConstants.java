package com.constant;

/**
 * 订单常量类
 */
public class OrderConstants {

    /**
     * 订单状态
     */
    // 待付款
    public static final Integer ORDER_STATUS_PENDING_PAYMENT = 0;
    // 待发货
    public static final Integer ORDER_STATUS_PENDING_DELIVERY = 1;
    // 待收货
    public static final Integer ORDER_STATUS_PENDING_RECEIPT = 2;
    // 已完成
    public static final Integer ORDER_STATUS_COMPLETED = 3;
    // 已取消
    public static final Integer ORDER_STATUS_CANCELLED = 4;

    /**
     * 分布式锁相关
     */
    // 订单创建锁前缀
    public static final String ORDER_LOCK_PREFIX = "order:lock:";
    // 锁超时时间（毫秒）
    public static final Long LOCK_EXPIRE_TIME = 30000L;
    // 锁重试等待时间（毫秒）
    public static final Long LOCK_WAIT_TIME = 5000L;
    // 锁最大重试次数
    public static final Integer LOCK_MAX_RETRY = 5;

    /**
     * 订单编号前缀
     */
    public static final String ORDER_NO_PREFIX = "QS";

    /**
     * 错误提示信息
     */
    public static final String MSG_LOGIN_REQUIRED = "请先登录";
    public static final String MSG_SELECT_PRODUCT = "请选择商品";
    public static final String MSG_DUPLICATE_ORDER = "请勿重复下单";
    public static final String MSG_PRODUCT_NOT_EXIST = "商品不存在";
    public static final String MSG_STOCK_INSUFFICIENT = "库存不足";
    public static final String MSG_STOCK_DEDUCT_FAILED = "库存扣减失败,请重试";
    public static final String MSG_ORDER_CREATE_FAILED = "创建订单失败,请稍后重试";
    public static final String MSG_ORDER_CREATE_EXCEPTION = "创建订单异常,请稍后重试";
    public static final String MSG_USER_ID_REQUIRED = "用户ID不能为空";
    public static final String MSG_PAGE_NUM_INVALID = "页码不能小于1";
    public static final String MSG_PAGE_SIZE_INVALID = "页大小不能小于1";
    public static final String MSG_ORDER_NO_REQUIRED = "订单号不能为空";
    public static final String MSG_ORDER_NOT_EXIST = "订单不存在";
    public static final String MSG_ORDER_STATUS_ERROR = "订单状态错误";
    public static final String MSG_ORDER_CANCEL_FAILED = "订单取消失败";
    public static final String MSG_ORDER_CANCEL_SUCCESS = "订单取消成功";
    public static final String MSG_ORDER_DELETE_FAILED = "订单删除失败";
    public static final String MSG_ORDER_DELETE_SUCCESS = "订单删除成功";
    public static final String MSG_ONLY_PENDING_PAYMENT_CANCEL = "仅待付款订单可取消";
    public static final String MSG_ONLY_FINISHED_DELETABLE = "仅已完成或已取消的订单可删除";

    /**
     * 支付相关
     */
    public static final String MSG_PAY_NOTIFY_SUCCESS = "支付成功";
    public static final String MSG_PAY_NOTIFY_FAILED = "支付失败";
    public static final String MSG_PAY_TYPE_NOT_NULL = "支付方式不能为空";
    public static final String MSG_PAY_TYPE_ERROR = "支付方式错误";
    public static final String MSG_PAY_NOTIFY_ERROR = "支付回调异常";

}
