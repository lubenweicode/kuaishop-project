package Seckill;

public class AdminConstants {

    public static final Integer PARAM_ERROR = 400;
    public static final String PARAM_ERROR_MESSAGE = "参数错误";
    public static final String PARAM_ERROR_USERNAME_NULL_MESSAGE = "用户名不能为空";
    public static final String PARAM_ERROR_PHONE_NULL_MESSAGE = "手机号不能为空";
    public static final String PARAM_ERROR_PASSWORD_NULL_MESSAGE = "密码不能为空";
    // 用户名已存在
    public static final int USERNAME_EXIST = 4001;
    public static final String USERNAME_EXIST_MESSAGE = "用户名已存在";
    // 手机号已存在
    public static final int PHONE_EXIST = 4002;
    public static final String PHONE_EXIST_MESSAGE = "手机号已存在";

    // 邮箱已存在
    public static final int EMAIL_EXIST = 4003;
    public static final String EMAIL_EXIST_MESSAGE = "邮箱已存在";

    // 密码格式错误
    public static final int PASSWORD_FORMAT_ERROR = 4004;
    public static final String PASSWORD_FORMAT_ERROR_MESSAGE = "密码格式错误";

    // 用户名格式错误
    public static final int USERNAME_FORMAT_ERROR = 4005;
    public static final String USERNAME_FORMAT_ERROR_MESSAGE = "用户名格式错误";

    // 用户名/手机号/邮箱已存在
    public static final Integer USERNAME_PHONE_EMAIL_EXIST = 4007;
    public static final String USERNAME_PHONE_EMAIL_EXIST_MESSAGE = "用户名/手机号/邮箱已存在";

    // 手机号格式错误
    public static final int PHONE_FORMAT_ERROR = 4006;
    public static final String PHONE_FORMAT_ERROR_MESSAGE = "手机号格式错误";
    public static final Integer REGISTER_FAILED = 500;
    public static final String REGISTER_FAILED_MESSAGE = "注册失败";

    // 登录失败次数限制常量
    public static final String LOGIN_FAIL_COUNT_KEY_PREFIX = "login:fail:count:";
    public static final String LOGIN_LOCK_KEY_PREFIX = "login:lock:status:";
    public static final int MAX_LOGIN_FAIL_TIMES = 5; // 最大失败次数
    public static final long LOGIN_LOCK_DURATION = 30 * 60; // 锁定时长（30分钟，单位：秒）
    public static final String LOGIN_LOCKED_TIP = "登录失败次数过多，账号已锁定，剩余%d分钟请稍后重试";
    public static final String USER_PWD_ERROR_MSG = "用户名或密码错误";
    public static final String LOGIN_LOCKED_MSG =  "登录失败次数过多，账号已锁定，请稍后重试";

    private AdminConstants(){}
}
