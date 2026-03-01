package com.constant;

/**
 * 支付模块常量定义
 *
 * <p>包含支付方式、支付状态、错误码及提示信息
 * 错误码采用自定义业务码（6001-6099），与订单模块（10001-10099）区分
 *
 * @author your-name
 * @since 1.0
 */
public class PayMentConstants {

    /**
     * ==================== 支付方式 ====================
     * 定义系统支持的支付渠道
     */

    /** 微信支付（1） */
    public static final Integer PAY_TYPE_WECHAT = 1;

    /** 支付宝支付（2） */
    public static final Integer PAY_TYPE_ALIPAY = 2;

    /** 银联支付（3）- 预留 */
    public static final Integer PAY_TYPE_UNIONPAY = 3;

    /** 余额支付（4）- 预留 */
    public static final Integer PAY_TYPE_BALANCE = 4;


    /**
     * ==================== 支付状态 ====================
     * 支付交易的生命周期状态
     */

    /** 支付状态：待支付（0） */
    public static final Integer PAY_STATUS_WAITING = 0;

    /** 支付状态：支付中（1）- 防止重复回调 */
    public static final Integer PAY_STATUS_PROCESSING = 1;

    /** 支付状态：支付成功（2） */
    public static final Integer PAY_STATUS_SUCCESS = 2;

    /** 支付状态：支付失败（3） */
    public static final Integer PAY_STATUS_FAILED = 3;

    /** 支付状态：已退款（4） */
    public static final Integer PAY_STATUS_REFUNDED = 4;

    /** 支付状态：已关闭（5）- 超时未支付 */
    public static final Integer PAY_STATUS_CLOSED = 5;


    /**
     * ==================== 支付错误码 (6001-6099) ====================
     * 支付模块专用错误码，用于精确标识支付过程中的各类异常
     */

    /* ---------- 参数校验错误 (6001-6010) ---------- */

    /** 6001 用户ID不能为空 */
    public static final int ERROR_CODE_USER_ID_REQUIRED = 6001;
    public static final String MSG_USER_ID_REQUIRED = "用户ID不能为空";

    /** 6002 订单号不能为空 */
    public static final int ERROR_CODE_ORDER_NO_REQUIRED = 6002;
    public static final String MSG_ORDER_NO_REQUIRED = "订单号不能为空";

    /** 6003 支付方式不能为空 */
    public static final int ERROR_CODE_PAY_TYPE_REQUIRED = 6003;
    public static final String MSG_PAY_TYPE_REQUIRED = "请选择支付方式";

    /** 6004 不支持的支付方式 */
    public static final int ERROR_CODE_PAY_TYPE_INVALID = 6004;
    public static final String MSG_PAY_TYPE_INVALID = "不支持的支付方式";

    /** 6005 支付金额不能为空 */
    public static final int ERROR_CODE_AMOUNT_REQUIRED = 6005;
    public static final String MSG_AMOUNT_REQUIRED = "支付金额不能为空";

    /** 6006 支付金额必须大于0 */
    public static final int ERROR_CODE_AMOUNT_INVALID = 6006;
    public static final String MSG_AMOUNT_INVALID = "支付金额必须大于0";

    /* ---------- 订单校验错误 (6011-6020) ---------- */

    /** 6011 订单不存在 */
    public static final int ERROR_CODE_ORDER_NOT_EXIST = 6011;
    public static final String MSG_ORDER_NOT_EXIST = "订单不存在";

    /** 6012 订单不属于当前用户 */
    public static final int ERROR_CODE_ORDER_NOT_BELONG_TO_USER = 6012;
    public static final String MSG_ORDER_NOT_BELONG_TO_USER = "订单不属于当前用户";

    /** 6013 订单状态不允许支付 */
    public static final int ERROR_CODE_ORDER_STATUS_INVALID = 6013;
    public static final String MSG_ORDER_STATUS_INVALID = "当前订单状态不允许支付";

    /** 6014 订单已支付，请勿重复支付 */
    public static final int ERROR_CODE_ORDER_ALREADY_PAID = 6014;
    public static final String MSG_ORDER_ALREADY_PAID = "订单已支付，请勿重复支付";

    /** 6015 订单已超时关闭 */
    public static final int ERROR_CODE_ORDER_CLOSED = 6015;
    public static final String MSG_ORDER_CLOSED = "订单已超时关闭";

    /* ---------- 支付处理错误 (6021-6040) ---------- */

    /** 6021 支付创建失败 */
    public static final int ERROR_CODE_PAY_CREATE_FAILED = 6021;
    public static final String MSG_PAY_CREATE_FAILED = "支付创建失败，请稍后重试";

    /** 6022 支付处理中，请勿重复提交 */
    public static final int ERROR_CODE_PAY_PROCESSING = 6022;
    public static final String MSG_PAY_PROCESSING = "支付处理中，请勿重复提交";

    /** 6023 支付失败 */
    public static final int ERROR_CODE_PAY_FAILED = 6023;
    public static final String MSG_PAY_FAILED = "支付失败";

    /** 6024 支付超时 */
    public static final int ERROR_CODE_PAY_TIMEOUT = 6024;
    public static final String MSG_PAY_TIMEOUT = "支付超时，请重新发起";

    /** 6025 支付金额与订单金额不符 */
    public static final int ERROR_CODE_AMOUNT_MISMATCH = 6025;
    public static final String MSG_AMOUNT_MISMATCH = "支付金额与订单金额不符";

    /* ---------- 回调处理错误 (6041-6050) ---------- */

    /** 6041 支付回调参数错误 */
    public static final int ERROR_CODE_NOTIFY_PARAM_ERROR = 6041;
    public static final String MSG_NOTIFY_PARAM_ERROR = "支付回调参数错误";

    /** 6042 支付回调验签失败 */
    public static final int ERROR_CODE_NOTIFY_SIGN_ERROR = 6042;
    public static final String MSG_NOTIFY_SIGN_ERROR = "支付回调验签失败";

    /** 6043 支付回调处理失败 */
    public static final int ERROR_CODE_NOTIFY_PROCESS_ERROR = 6043;
    public static final String MSG_NOTIFY_PROCESS_ERROR = "支付回调处理失败";

    /** 6044 不支持的支付渠道回调 */
    public static final int ERROR_CODE_CHANNEL_NOT_SUPPORT = 6044;
    public static final String MSG_CHANNEL_NOT_SUPPORT = "不支持的支付渠道";

    /* ---------- 系统错误 (6051-6060) ---------- */

    /** 6051 支付系统繁忙 */
    public static final int ERROR_CODE_PAY_SYSTEM_BUSY = 6051;
    public static final String MSG_PAY_SYSTEM_BUSY = "支付系统繁忙，请稍后重试";

    /** 6052 支付异常 */
    public static final int ERROR_CODE_PAY_EXCEPTION = 6052;
    public static final String MSG_PAY_EXCEPTION = "支付异常，请联系客服";


    /**
     * ==================== 成功码 ====================
     * 支付操作成功返回码
     */

    /** 6200 支付创建成功 */
    public static final int SUCCESS_CODE_PAY_CREATE = 6200;
    public static final String SUCCESS_MSG_PAY_CREATE = "支付创建成功";

    /** 6201 支付成功 */
    public static final int SUCCESS_CODE_PAY_SUCCESS = 6201;
    public static final String SUCCESS_MSG_PAY_SUCCESS = "支付成功";

    /** 6202 查询支付状态成功 */
    public static final int SUCCESS_CODE_QUERY_SUCCESS = 6202;
    public static final String SUCCESS_MSG_QUERY_SUCCESS = "查询成功";

    /** 6203 支付回调处理成功 */
    public static final int SUCCESS_CODE_NOTIFY_SUCCESS = 6203;
    public static final String SUCCESS_MSG_NOTIFY_SUCCESS = "回调处理成功";


    /**
     * ==================== 支付渠道标识 ====================
     * 用于支付回调时识别渠道
     */

    /** 微信支付渠道标识 */
    public static final String CHANNEL_WECHAT = "WECHAT";

    /** 支付宝支付渠道标识 */
    public static final String CHANNEL_ALIPAY = "ALIPAY";

    /** 银联支付渠道标识 */
    public static final String CHANNEL_UNIONPAY = "UNIONPAY";


    /**
     * ==================== Redis缓存配置 ====================
     * 支付相关缓存key前缀
     */

    /** 支付处理中锁前缀：防止重复回调，key = PAY_PROCESSING_LOCK_PREFIX + orderNo */
    public static final String PAY_PROCESSING_LOCK_PREFIX = "pay:processing:lock:";

    /** 支付结果缓存前缀：key = PAY_RESULT_PREFIX + orderNo */
    public static final String PAY_RESULT_PREFIX = "pay:result:";

    /** 支付处理中锁过期时间（秒）：30秒，需大于支付回调处理时间 */
    public static final long PAY_LOCK_EXPIRE_TIME = 30;


    /**
     * ==================== 支付URL模板 ====================
     * 各支付渠道的URL或二维码模板
     */

    /** 微信支付URL模板（示例） */
    public static final String WECHAT_PAY_URL_TEMPLATE = "weixin://wxpay/bizpayurl?pr=%s";

    /** 微信支付二维码模板（示例）- 使用订单号作为参数 */
    public static final String WECHAT_QR_CODE_TEMPLATE = "https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=6&orderNo=%s";

    /** 支付宝支付URL模板（示例） */
    public static final String ALIPAY_PAY_URL_TEMPLATE = "zhifubao://zhifubao/bizpayurl?pr=%s";

    /** 支付宝支付二维码模板（示例）- 使用订单号作为参数 */
    public static final String ALIPAY_QR_CODE_TEMPLATE = "https://docs.open.alipay.com/api_1/alipay.trade.precreate?orderNo=%s";


    /**
     * 获取支付方式的中文描述
     *
     * @param payType 支付方式编码
     * @return 支付方式中文名称
     */
    public static String getPayTypeText(Integer payType) {
        if (payType == null) {
            return "未知";
        }
        return switch (payType) {
            case 1 -> "微信支付";
            case 2 -> "支付宝支付";
            case 3 -> "银联支付";
            case 4 -> "余额支付";
            default -> "未知支付方式";
        };
    }

    /**
     * 获取支付状态的中文描述
     *
     * @param status 支付状态码
     * @return 支付状态中文描述
     */
    public static String getPayStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "支付中";
            case 2 -> "支付成功";
            case 3 -> "支付失败";
            case 4 -> "已退款";
            case 5 -> "已关闭";
            default -> "未知状态";
        };
    }

    /**
     * 私有构造函数，防止实例化
     */
    private PayMentConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}