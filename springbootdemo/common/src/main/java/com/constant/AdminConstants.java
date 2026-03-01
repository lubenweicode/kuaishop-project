package com.constant;

/**
 * 管理员/用户模块常量定义
 *
 * <p>包含用户注册、登录、账号安全相关的常量配置
 * 错误码规则：
 * <ul>
 *   <li>4xx - HTTP标准状态码，表示参数类错误</li>
 *   <li>400x - 自定义业务码，表示具体的业务校验错误</li>
 *   <li>5xx - HTTP标准状态码，表示系统内部错误</li>
 * </ul>
 *
 * @author your-name
 * @since 1.0
 */
public class AdminConstants {

    /**
     * ==================== HTTP标准状态码 ====================
     * 遵循RESTful API设计规范
     */

    /** 400 参数错误：请求参数基本校验失败（如必填项为空） */
    public static final Integer PARAM_ERROR = 400;
    public static final String MSG_PARAM_ERROR_MESSAGE = "参数错误";

    /** 500 注册失败：系统内部异常导致注册未完成 */
    public static final Integer REGISTER_FAILED = 500;
    public static final String MSG_REGISTER_FAILED_MESSAGE = "注册失败";

    /**
     * ==================== 字段级参数校验 ====================
     * 单个字段的必填校验，均返回400
     */

    /** 400 用户名不能为空：注册/登录时必填 */
    public static final String MSG_PARAM_ERROR_USERNAME_NULL_MESSAGE = "用户名不能为空";

    /** 400 手机号不能为空：注册/绑定手机时必填 */
    public static final String MSG_PARAM_ERROR_PHONE_NULL_MESSAGE = "手机号不能为空";

    /** 400 密码不能为空：注册/登录/修改密码时必填 */
    public static final String MSG_PARAM_ERROR_PASSWORD_NULL_MESSAGE = "密码不能为空";

    /**
     * ==================== 自定义业务错误码 (4001-4099) ====================
     * 用于更精细的业务校验，前端可根据具体code做针对性提示
     */

    /* ---------- 账号唯一性校验 (4001-4010) ---------- */

    /** 4001 用户名已存在：注册时用户名已被占用 */
    public static final int USERNAME_EXIST = 4001;
    public static final String MSG_USERNAME_EXIST_MESSAGE = "用户名已存在";

    /** 4002 手机号已存在：注册/绑定时手机号已被占用 */
    public static final int PHONE_EXIST = 4002;
    public static final String MSG_PHONE_EXIST_MESSAGE = "手机号已存在";

    /** 4003 邮箱已存在：注册/绑定时邮箱已被占用 */
    public static final int EMAIL_EXIST = 4003;
    public static final String MSG_EMAIL_EXIST_MESSAGE = "邮箱已存在";

    /** 4007 用户名/手机号/邮箱已存在：注册时三者至少其一已被占用 */
    public static final Integer USERNAME_PHONE_EMAIL_EXIST = 4007;
    public static final String MSG_USERNAME_PHONE_EMAIL_EXIST_MESSAGE = "用户名/手机号/邮箱已存在";

    /* ---------- 字段格式校验 (4011-4020) ---------- */

    /** 4004 密码格式错误：不符合密码复杂度要求 */
    public static final int PASSWORD_FORMAT_ERROR = 4004;
    public static final String MSG_PASSWORD_FORMAT_ERROR_MESSAGE = "密码格式错误";
    /** 密码正则：3-20位，支持字母、数字、下划线、中文 */
    public static final String PASSWORD_REGEX = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]{3,20}$";

    /** 4005 用户名格式错误：不符合用户名命名规则 */
    public static final int USERNAME_FORMAT_ERROR = 4005;
    public static final String MSG_USERNAME_FORMAT_ERROR_MESSAGE = "用户名格式错误";
    /** 用户名正则：3-20位，支持字母、数字、下划线、中文 */
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]{3,20}$";

    /** 4006 手机号格式错误：不符合中国大陆手机号规则 */
    public static final int PHONE_FORMAT_ERROR = 4006;
    public static final String MSG_PHONE_FORMAT_ERROR_MESSAGE = "手机号格式错误";
    /** 手机号正则：1开头的11位数字，第二位3-9 */
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /** 4008 邮箱格式错误：不符合标准邮箱格式 */
    public static final int EMAIL_FORMAT_ERROR = 4008;
    public static final String MSG_EMAIL_FORMAT_ERROR_MESSAGE = "邮箱格式错误";
    /** 邮箱正则：标准邮箱格式校验 */
    public static final String EMAIL_REGEX = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    /**
     * ==================== 登录安全配置 ====================
     * 登录失败次数限制及账号锁定机制
     */

    /* ---------- Redis key前缀 ---------- */
    /** 登录失败次数的Redis key前缀：完整key = PREFIX + 用户名/手机号/邮箱 */
    public static final String LOGIN_FAIL_COUNT_KEY_PREFIX = "login:fail:count:";

    /** 登录锁定状态的Redis key前缀：完整key = PREFIX + 用户名/手机号/邮箱 */
    public static final String LOGIN_LOCK_KEY_PREFIX = "login:lock:status:";

    /* ---------- 锁定策略 ---------- */
    /** 最大允许的连续登录失败次数：超过此次数将触发账号锁定 */
    public static final int MAX_LOGIN_FAIL_TIMES = 5;

    /** 账号锁定持续时间（秒）：30分钟 = 1800秒 */
    public static final long LOGIN_LOCK_DURATION = 30 * 60;

    /* ---------- 登录提示信息 ---------- */
    /** 登录失败提示：用户名或密码错误（不明确提示具体哪个错，防止账号探测） */
    public static final String MSG_USER_PWD_ERROR_MSG = "用户名或密码错误";

    /** 账号锁定提示（无剩余时间）：用于接口返回 */
    public static final String MSG_LOGIN_LOCKED_MSG = "登录失败次数过多，账号已锁定，请稍后重试";

    /**
     * 账号锁定提示模板（含剩余时间）：用于前端展示倒计时
     * 使用时需用 String.format(MSG_LOGIN_LOCKED_TIP, minutes) 填充剩余分钟数
     */
    public static final String MSG_LOGIN_LOCKED_TIP = "登录失败次数过多，账号已锁定，剩余%d分钟请稍后重试";

    /**
     * 私有构造函数，防止实例化
     */
    private AdminConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}