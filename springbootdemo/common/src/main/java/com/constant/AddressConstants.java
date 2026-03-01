package com.constant;

/**
 * 地址模块常量定义
 *
 * <p>包含地址管理相关的错误码、提示信息
 * 错误码采用HTTP状态码标准：
 * <ul>
 *   <li>2xx - 成功：操作正常完成</li>
 *   <li>4xx - 客户端错误：请求参数校验失败、权限问题等</li>
 *   <li>5xx - 服务端错误：系统内部异常</li>
 * </ul>
 *
 * @author your-name
 * @since 1.0
 */
public class AddressConstants {

    /**
     * ==================== 客户端错误 (4xx) ====================
     * 由用户请求参数错误或权限问题导致，前端应根据具体code给出相应提示
     */

    /** 401 未登录：用户需要先登录才能操作地址簿 */
    public static final String MSG_LOGIN_ERROR = "请先登录";
    public static final int CODE_LOGIN_ERROR = 401;

    /* ---------- 地址字段校验 (400) ---------- */
    /** 400 收货人不能为空：收货人姓名必填 */
    public static final String MSG_RECEIVER_NAME_REQUIRED = "收货人不能为空";
    public static final int CODE_RECEIVER_NAME_REQUIRED = 400;

    /** 400 收货电话不能为空：联系方式必填，用于物流通知 */
    public static final String MSG_RECEIVER_PHONE_REQUIRED = "收货电话不能为空";
    public static final int CODE_RECEIVER_PHONE_REQUIRED = 400;

    /** 400 省份不能为空：所属省份必选 */
    public static final String MSG_PROVINCE_REQUIRED = "省份不能为空";
    public static final int CODE_PROVINCE_REQUIRED = 400;

    /** 400 城市不能为空：所属城市必选 */
    public static final String MSG_CITY_REQUIRED = "城市不能为空";
    public static final int CODE_CITY_REQUIRED = 400;

    /** 400 区县不能为空：所属区县必选 */
    public static final String MSG_DISTRICT_REQUIRED = "区县不能为空";
    public static final int CODE_DISTRICT_REQUIRED = 400;

    /** 400 详细地址不能为空：具体门牌号、街道等信息必填 */
    public static final String MSG_DETAIL_ADDRESS_REQUIRED = "详细地址不能为空";
    public static final int CODE_DETAIL_ADDRESS_REQUIRED = 400;

    /** 400 地址ID不能为空：更新、删除、设置默认地址时必传 */
    public static final String MSG_ADDRESS_ID_REQUIRED = "地址ID不能为空";
    public static final int CODE_ADDRESS_ID_REQUIRED = 400;

    /* ---------- 地址业务错误 (400) ---------- */
    /** 400 地址不存在：查询或操作的地址记录已被删除 */
    public static final String MSG_ADDRESS_NOT_FOUND = "地址不存在";
    public static final int CODE_ADDRESS_NOT_FOUND = 400;

    /** 400 地址不属于当前用户：尝试操作其他用户的地址（权限校验） */
    public static final String MSG_ADDRESS_NOT_BELONG_TO_USER = "地址不属于当前用户";
    public static final int CODE_ADDRESS_NOT_BELONG_TO_USER = 400;

    /** 400 获取用户地址列表为空：该用户尚未添加任何收货地址 */
    public static final String MSG_LIST_ADDRESS_EMPTY = "获取用户地址列表为空";
    public static final int CODE_LIST_ADDRESS_EMPTY = 400;

    /**
     * ==================== 服务端错误 (5xx) ====================
     * 由服务器内部异常导致，前端应提示“系统繁忙，请稍后重试”
     */

    /** 500 添加地址失败：可能原因包括数据库异常等 */
    public static final String MSG_ADD_ADDRESS_FAIL = "添加地址失败";
    public static final int CODE_ADD_ADDRESS_FAIL = 500;

    /** 500 设置默认地址失败：更新默认地址时发生系统异常 */
    public static final String MSG_SET_DEFAULT_ADDRESS_FAIL = "设置默认地址失败";
    public static final int CODE_SET_DEFAULT_ADDRESS_FAIL = 500;

    /** 500 获取用户地址列表失败：查询地址列表时发生系统异常 */
    public static final String MSG_LIST_ADDRESS_FAIL = "获取用户地址列表失败";
    public static final int CODE_LIST_ADDRESS_FAIL = 500;

    /**
     * ==================== 成功提示 (2xx) ====================
     * 操作成功完成，data字段包含具体返回数据
     */

    /** 200 添加地址成功 */
    public static final String MSG_ADD_ADDRESS_SUCCESS = "添加地址成功";
    public static final int CODE_ADD_ADDRESS_SUCCESS = 200;

    /** 200 设置默认地址成功 */
    public static final String MSG_SET_DEFAULT_ADDRESS_SUCCESS = "设置默认地址成功";
    public static final int CODE_SET_DEFAULT_ADDRESS_SUCCESS = 200;

    /** 200 获取用户地址列表成功（可能为空数组，但请求处理成功） */
    public static final String MSG_LIST_ADDRESS_SUCCESS = "获取用户地址列表成功";
    public static final int CODE_LIST_ADDRESS_SUCCESS = 200;

    /**
     * 私有构造函数，防止实例化
     */
    private AddressConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}