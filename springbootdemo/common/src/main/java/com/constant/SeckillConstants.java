package com.constant;

/**
 * 秒杀模块核心常量定义
 *
 * <p>包含秒杀活动的Redis键设计、错误码、限购规则等核心配置。
 * Redis键采用两种风格：
 * <ul>
 *   <li>基础风格：简洁高效，适用于程序内部频繁拼接</li>
 *   <li>高辨识度风格：语义化强，适用于Redis可视化工具查看和调试</li>
 * </ul>
 *
 * @author your-name
 * @since 1.0
 */
public class SeckillConstants {

    /**
     * ==================== Redis Key 前缀（基础风格） ====================
     * 简洁高效的键前缀设计，适用于程序内部频繁拼接场景
     * 所有key均以"seckill:"为命名空间，便于Redis管理
     */

    /** 秒杀活动根前缀：所有秒杀相关key以此开头 */
    public static final String SECKILL_ACTIVITY_PREFIX = "seckill:activity:";

    /**
     * 秒杀商品库存Key（格式化模板）
     * 使用示例：String.format(SECKILL_ACTIVITY_STOCK_KEY, activityId, productId)
     * 结果示例：seckill:activity:1001:product:20001:stock
     */
    public static final String SECKILL_ACTIVITY_STOCK_KEY = SECKILL_ACTIVITY_PREFIX + "%d:product:%d:stock";

    /**
     * 秒杀分布式锁Key（格式化模板）
     * 使用示例：String.format(SECKILL_LOCK_KEY, activityId, productId)
     * 结果示例：seckill:activity:1001:product:20001:lock
     */
    public static final String SECKILL_LOCK_KEY = SECKILL_ACTIVITY_PREFIX + "%d:product:%d:lock";

    /**
     * 秒杀活动详情Key（格式化模板）
     * 使用示例：String.format(SECKILL_ACTIVITY_KEY, activityId)
     * 结果示例：seckill:activity:1001
     */
    public static final String SECKILL_ACTIVITY_KEY = SECKILL_ACTIVITY_PREFIX + "%d";

    /**
     * 秒杀活动列表Key（按状态分类）
     * 使用示例：String.format(SECKILL_ACTIVITY_LIST_KEY, status)
     * 结果示例：seckill:activity:status:1:id:
     * 说明：存储指定状态的活动ID集合，如进行中(1)的活动列表
     */
    public static final String SECKILL_ACTIVITY_LIST_KEY = SECKILL_ACTIVITY_PREFIX + "status:%d:id:";

    /**
     * ==================== Redis Key（高辨识度风格） ====================
     * 语义化强的键设计，虽然较长但可读性好，便于Redis可视化工具查看
     * 采用"字段名:值"的交替格式，一眼就能看懂每个字段的含义
     */

    /**
     * 秒杀商品缓存键（高辨识度）
     * 格式：seckill:activity:product:status:{status}:activityId:{activityId}:productId:{productId}
     * 语义：秒杀-活动-商品-状态-{状态值}-活动编号-{活动ID}-商品编号-{商品ID}
     * 示例：seckill:activity:product:status:1:activityId:1001:productId:20001
     */
    public static final String SECKILL_ACTIVITY_PRODUCT_KEY = "seckill:activity:product:status:%d:activityId:%d:productId:%d";

    /**
     * 秒杀商品库存缓存键（高辨识度）
     * 格式：seckill:activity:product:stock:activityId:{activityId}:productId:{productId}
     * 语义：秒杀-活动-商品-库存-活动编号-{活动ID}-商品编号-{商品ID}
     * 示例：seckill:activity:product:stock:activityId:1001:productId:20001
     */
    public static final String SECKILL_ACTIVITY_PRODUCT_STOCK_KEY = "seckill:activity:product:stock:activityId:%d:productId:%d";

    /**
     * 秒杀用户限购缓存键（高辨识度）
     * 格式：seckill:user:limiter:activityId:{activityId}:userId:{userId}
     * 语义：秒杀-用户-限购-活动编号-{活动ID}-用户编号-{用户ID}
     * 示例：seckill:user:limiter:activityId:1001:userId:10001
     */
    public static final String SECKILL_USER_LIMIT_KEY = "seckill:user:limiter:activityId:%d:userId:%d";

    /**
     * 秒杀用户订单缓存键（高辨识度）
     * 格式：seckill:user:order:activityId:{activityId}:productId:{productId}:userId:{userId}
     * 语义：秒杀-用户-订单-活动编号-{活动ID}-商品编号-{商品ID}-用户编号-{用户ID}
     * 示例：seckill:user:order:activityId:1001:productId:20001:userId:10001
     */
    public static final String SECKILL_USER_ORDER_KEY = "seckill:user:order:activityId:%d:productId:%d:userId:%d";

    /**
     * 秒杀商品通配查询键（用于模糊匹配）
     * 格式：seckill:activity:product:status:{status}:activityId:{activityId}:productId:
     * 说明：结尾的冒号用于配合Redis的KEYS或SCAN命令，查询某个活动下的所有商品
     * 示例：seckill:activity:product:status:1:activityId:1001:productId:*
     */
    public static final String SECKILL_ACTIVITY_PRODUCT_PATTERN_KEY = "seckill:activity:product:status:%d:activityId:%d:productId:";

    /**
     * ==================== 秒杀业务错误码 (5001-5099) ====================
     * 秒杀模块专用错误码，用于精确标识秒杀过程中的各类异常
     */

    /** 5001 活动状态异常：活动未开始或已结束 */
    public static final Integer ERROR_ACTIVITY_STATUS = 5001;

    /** 5002 库存不足：商品秒杀库存已售罄 */
    public static final Integer ERROR_STOCK_INSUFFICIENT = 5002;

    /** 5003 超出个人限购：用户购买数量超过活动限制 */
    public static final Integer ERROR_USER_LIMIT_EXCEED = 5003;

    /** 5004 重复秒杀：用户已参与过该商品的秒杀 */
    public static final Integer ERROR_REPEAT_ORDER = 5004;

    /** 5005 系统繁忙：Redis或数据库操作异常，通常触发重试机制 */
    public static final Integer ERROR_SYSTEM_BUSY = 5005;

    /** 5006 活动不存在：请求的秒杀活动ID无效或已删除 */
    public static final Integer ERROR_ACTIVITY_EXCEPTION = 5006;

    /**
     * ==================== 秒杀业务常量 ====================
     */

    /** 用户默认限购数量：每个用户默认限购1件 */
    public static final Integer USER_LIMIT_DEFAULT = 1;

    /**
     * 支付接口URL前缀
     * 实际使用时需拼接订单号：PAY_URL_PREFIX + orderNo
     * TODO: 上线前需替换为实际域名或从配置文件读取
     */
    public static final String PAY_URL_PREFIX = "http://localhost:8080/api/v1/payment/pay?orderNo=";

    /**
     * 获取活动状态的中文描述
     *
     * @param status 活动状态码（0-未开始，1-进行中，2-已结束）
     * @return 状态的中文描述
     */
    public static String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "未开始";
            case 1 -> "进行中";
            case 2 -> "已结束";
            default -> "未知状态";
        };
    }

    /**
     * 私有构造函数，防止实例化
     */
    private SeckillConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}