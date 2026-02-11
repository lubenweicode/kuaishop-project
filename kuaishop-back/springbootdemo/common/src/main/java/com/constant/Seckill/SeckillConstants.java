package com.constant.Seckill;

/**
 * 秒杀核心常量
 */
public class SeckillConstants {

    // ==================== Redis Key 前缀（修正：与初始化器存储结构对齐） ====================
    // 活动相关前缀
    public static final String SECKILL_ACTIVITY_PREFIX = "seckill:activity:";
    // 商品库存Key：拼接规则 -> SECKILL_ACTIVITY_STOCK_KEY + 活动ID + ":" + 商品ID
    public static final String SECKILL_ACTIVITY_STOCK_KEY = SECKILL_ACTIVITY_PREFIX + "%d:product:%d:stock";

    // 分布式锁Key：拼接规则 -> SECKILL_LOCK_KEY + 活动ID + ":" + 商品ID
    public static final String SECKILL_LOCK_KEY = SECKILL_ACTIVITY_PREFIX + "%d:product:%d:lock";

    // 活动完整信息Key（兼容业务服务checkActivity方法）：拼接规则 -> SECKILL_ACTIVITY_KEY + 活动ID
    public static final String SECKILL_ACTIVITY_KEY = SECKILL_ACTIVITY_PREFIX + "%d";
    // 活动列表缓存Key：拼接规则 -> SECKILL_ACTIVITY_LIST_KEY + 状态码
    public static final String SECKILL_ACTIVITY_LIST_KEY = SECKILL_ACTIVITY_PREFIX + "status:%d:id:";

    // ==================== 错误码 ====================
    public static final Integer ERROR_ACTIVITY_STATUS = 5001; // 活动未开始或已结束
    public static final Integer ERROR_STOCK_INSUFFICIENT = 5002; // 库存不足
    public static final Integer ERROR_USER_LIMIT_EXCEED = 5003; // 超出个人限购
    public static final Integer ERROR_REPEAT_ORDER = 5004; // 重复秒杀
    public static final Integer ERROR_SYSTEM_BUSY = 5005; // 系统繁忙
    public static final Integer ERROR_ACTIVITY_EXCEPTION = 5006; // 活动不存在

    // ==================== 其他常量 ====================
    public static final Integer USER_LIMIT_DEFAULT = 1; // 用户默认限购数量
    public static final String PAY_URL_PREFIX = "http://localhost:8080/api/v1/payment/pay?orderNo="; // 支付接口前缀

    /**
     * 秒杀商品缓存键（高辨识度）
     * 格式化后：seckill:activity:product:status:1:activityId:1001:productId:20001
     * 语义：秒杀-活动-商品-状态-1-活动编号-1001-商品编号-20001
     */
    public static final String SECKILL_ACTIVITY_PRODUCT_KEY = "seckill:activity:product:status:%d:activityId:%d:productId:%d";

    /**
     * 秒杀商品库存缓存键（高辨识度）
     * 格式化后：seckill:activity:product:stock:activityId:1001:productId:20001
     * 语义：秒杀-活动-商品-库存-活动编号-1001-商品编号-20001
     */
    public static final String SECKILL_ACTIVITY_PRODUCT_STOCK_KEY = "seckill:activity:product:stock:activityId:%d:productId:%d";

    /**
     * 秒杀用户限购缓存键（高辨识度）
     * 格式化后：seckill:user:limiter:activityId:1001:userId:10001
     * 语义：秒杀-用户-限购-活动编号-1001-用户编号-10001
     */
    public static final String SECKILL_USER_LIMIT_KEY = "seckill:user:limiter:activityId:%d:userId:%d";

    /**
     * 秒杀用户订单缓存键（高辨识度）
     * 格式化后：seckill:user:order:activityId:1001:userId:10001
     * 语义：秒杀-用户-订单-活动编号-1001-用户编号-10001
     */
    public static final String SECKILL_USER_ORDER_KEY = "seckill:user:order:activityId:%d:productId:%d:userId:%d";

    /**
     * 秒杀商品通配查询键（用于匹配某个活动下的所有商品）
     * 格式化后：seckill:activity:product:status:1:activityId:1001:productId:
     */
    public static final String SECKILL_ACTIVITY_PRODUCT_PATTERN_KEY = "seckill:activity:product:status:%d:activityId:%d:productId:";

    public static String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "未开始";
            case 1 -> "进行中";
            case 2 -> "已结束";
            default -> "未知";
        };
    }
}