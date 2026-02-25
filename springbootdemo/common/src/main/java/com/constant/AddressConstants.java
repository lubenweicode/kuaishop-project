package com.constant;

/**
 * 地址常量类
 *
 */
public class AddressConstants {

    // 登录错误信息
    public static final String MSG_LOGIN_ERROR = "请先登录";
    public static final int CODE_LOGIN_ERROR = 401;

    // 添加地址错误信息
    // 收货人不能为空
    public static final String MSG_RECEIVER_NAME_REQUIRED = "收货人不能为空";
    public static final int CODE_RECEIVER_NAME_REQUIRED = 400;

    // 收货电话不能为空
    public static final String MSG_RECEIVER_PHONE_REQUIRED = "收货电话不能为空";
    public static final int CODE_RECEIVER_PHONE_REQUIRED = 400;

    // 省份不能为空
    public static final String MSG_PROVINCE_REQUIRED = "省份不能为空";
    public static final int CODE_PROVINCE_REQUIRED = 400;

    // 城市不能为空
    public static final String MSG_CITY_REQUIRED = "城市不能为空";
    public static final int CODE_CITY_REQUIRED = 400;

    // 区县不能为空
    public static final String MSG_DISTRICT_REQUIRED = "区县不能为空";
    public static final int CODE_DISTRICT_REQUIRED = 400;

    // 详细地址不能为空
    public static final String MSG_DETAIL_ADDRESS_REQUIRED = "详细地址不能为空";
    public static final int CODE_DETAIL_ADDRESS_REQUIRED = 400;

    // 添加地址成功信息
    public static final String MSG_ADD_ADDRESS_SUCCESS = "添加地址成功";
    public static final int CODE_ADD_ADDRESS_SUCCESS = 200;

    // 添加地址失败信息
    public static final String MSG_ADD_ADDRESS_FAIL = "添加地址失败";
    public static final int CODE_ADD_ADDRESS_FAIL = 500;

    // 地址ID不能为空
    public static final String MSG_ADDRESS_ID_REQUIRED = "地址ID不能为空";
    public static final int CODE_ADDRESS_ID_REQUIRED = 400;

    // 地址不存在
    public static final String MSG_ADDRESS_NOT_FOUND = "地址不存在";
    public static final int CODE_ADDRESS_NOT_FOUND = 400;

    // 地址不属于当前用户
    public static final String MSG_ADDRESS_NOT_BELONG_TO_USER = "地址不属于当前用户";
    public static final int CODE_ADDRESS_NOT_BELONG_TO_USER = 400;

    // 设置默认地址失败
    public static final String MSG_SET_DEFAULT_ADDRESS_FAIL = "设置默认地址失败";
    public static final int CODE_SET_DEFAULT_ADDRESS_FAIL = 500;

    // 设置默认地址成功
    public static final String MSG_SET_DEFAULT_ADDRESS_SUCCESS = "设置默认地址成功";
    public static final int CODE_SET_DEFAULT_ADDRESS_SUCCESS = 200;

    // 获取用户地址列表成功
    public static final String MSG_LIST_ADDRESS_SUCCESS = "获取用户地址列表成功";
    public static final int CODE_LIST_ADDRESS_SUCCESS = 200;

    // 获取用户地址列表为空
    public static final String MSG_LIST_ADDRESS_EMPTY = "获取用户地址列表为空";
    public static final int CODE_LIST_ADDRESS_EMPTY = 400;

    // 获取用户地址列表失败
    public static final String MSG_LIST_ADDRESS_FAIL = "获取用户地址列表失败";
    public static final int CODE_LIST_ADDRESS_FAIL = 500;

}
