package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.constant.OrderConstants;
import com.constant.PayMentConstants;
import com.repository.mapper.OrderMapper;
import com.service.PayMentService;
import domain.entity.Orders;
import domain.payment.PayMentDTO;
import domain.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

@Service
@Slf4j
public class PayMentServiceImpl implements PayMentService {

    private final OrderMapper orderMapper;
    private static final String PAY_PLATFORM_SECRET = "pay_platform_secret";

    public PayMentServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    /**
     * 发起支付
     * @param payMentDTO
     * @return
     */
    @Override
    public Result pay(PayMentDTO payMentDTO) {
        // 1. 参数校验
        // 1.1 用户id不能为空
        if(payMentDTO.getUserId() == null){
            return Result.error(PayMentConstants.ERROR_CODE_USER_ID_REQUIRED,
                    PayMentConstants.MSG_USER_ID_REQUIRED);
        }

        // 1.2 订单号不能为空
        if(payMentDTO.getOrderNo() == null){
            return Result.error(PayMentConstants.ERROR_CODE_ORDER_NO_REQUIRED,
                    PayMentConstants.MSG_ORDER_NO_REQUIRED);
        }

        // 1.3 支付方式校验
        if(payMentDTO.getPayType() == null){
            return Result.error(PayMentConstants.ERROR_CODE_PAY_TYPE_REQUIRED,
                    PayMentConstants.MSG_PAY_TYPE_REQUIRED);
        }

        // 检查是否支持的支付方式
        if(!isSupportedPayType(payMentDTO.getPayType())){
            return Result.error(PayMentConstants.ERROR_CODE_PAY_TYPE_INVALID,
                    PayMentConstants.MSG_PAY_TYPE_INVALID);
        }

        // 2. 订单校验
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, payMentDTO.getUserId())
                .eq(Orders::getOrderNo, payMentDTO.getOrderNo());
        Orders order = orderMapper.selectOne(queryWrapper);

        // 2.1 订单不存在
        if (order == null) {
            log.warn("订单不存在: {}", payMentDTO.getOrderNo());
            return Result.error(PayMentConstants.ERROR_CODE_ORDER_NOT_EXIST,
                    PayMentConstants.MSG_ORDER_NOT_EXIST);
        }

        // 2.2 订单状态校验（只能支付待付款订单）
        if (!Objects.equals(order.getOrderStatus(), OrderConstants.ORDER_STATUS_PENDING_PAYMENT)) {
            log.warn("订单状态错误, 当前状态:{}", order.getOrderStatus());
            return Result.error(PayMentConstants.ERROR_CODE_ORDER_STATUS_INVALID,
                    PayMentConstants.MSG_ORDER_STATUS_INVALID);
        }

        // 2.3 修改订单状态
        order.setOrderStatus(OrderConstants.ORDER_STATUS_PENDING_DELIVERY);
        order.setPayType(payMentDTO.getPayType());

        // 生成交易号和签名
        String tradeNo = generateTradeNo();
        String tradeSign = generateSign(order.getOrderNo(), tradeNo);

        order.setOutTradeNo(tradeNo);
        order.setPaySecret(tradeSign);
        order.setUpdateTime(new Date());

        int update = orderMapper.updateById(order);
        if(update != 1) {
            log.warn("订单{}支付失败", order.getOrderNo());
            return Result.error(PayMentConstants.ERROR_CODE_PAY_CREATE_FAILED,
                    PayMentConstants.MSG_PAY_CREATE_FAILED);
        }

        // 3. 生成支付信息
        Map<String, Object> result = buildPayResult(order, payMentDTO.getPayType());

        return Result.success(PayMentConstants.SUCCESS_CODE_PAY_CREATE,
                PayMentConstants.SUCCESS_MSG_PAY_CREATE,
                result);
    }

    /**
     * 生成交易号
     */
    private String generateTradeNo() {
        return "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    /**
     * 生成签名
     */
    private String generateSign(String orderNo, String tradeNo) {
        String signStr = orderNo + tradeNo + PAY_PLATFORM_SECRET;
        return DigestUtils.md5DigestAsHex(signStr.getBytes());
    }

    /**
     * 判断是否支持的支付方式
     */
    private boolean isSupportedPayType(Integer payType) {
        return PayMentConstants.PAY_TYPE_WECHAT.equals(payType) ||
                PayMentConstants.PAY_TYPE_ALIPAY.equals(payType);
    }

    /**
     * 构建支付返回结果
     */
    private Map<String, Object> buildPayResult(Orders order, Integer payType) {
        String payUrl;
        String qrCode;

        if (PayMentConstants.PAY_TYPE_WECHAT.equals(payType)) {
            payUrl = String.format(PayMentConstants.WECHAT_PAY_URL_TEMPLATE, order.getOrderNo());
            // 使用订单号替代productId作为二维码参数
            qrCode = String.format(PayMentConstants.WECHAT_QR_CODE_TEMPLATE, order.getOrderNo());
        } else {
            payUrl = String.format(PayMentConstants.ALIPAY_PAY_URL_TEMPLATE, order.getOrderNo());
            // 使用订单号替代productId作为二维码参数
            qrCode = String.format(PayMentConstants.ALIPAY_QR_CODE_TEMPLATE, order.getOrderNo());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("payAmount", order.getPayAmount());
        result.put("payType", payType);
        result.put("payTypeText", PayMentConstants.getPayTypeText(payType));
        result.put("payUrl", payUrl);
        result.put("qrCode", qrCode);
        return result;
    }

    /**
     * 查询支付状态
     */
    @Override
    public Result getPayStatus(Long userId, String orderNo) {
        // 1. 参数校验
        if(userId == null){
            return Result.error(PayMentConstants.ERROR_CODE_USER_ID_REQUIRED,
                    PayMentConstants.MSG_USER_ID_REQUIRED);
        }
        if(orderNo == null){
            return Result.error(PayMentConstants.ERROR_CODE_ORDER_NO_REQUIRED,
                    PayMentConstants.MSG_ORDER_NO_REQUIRED);
        }

        // 2. 查询订单
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId)
                .eq(Orders::getOrderNo, orderNo);
        Orders order = orderMapper.selectOne(queryWrapper);

        if (order == null) {
            log.warn("订单不存在: {}", orderNo);
            return Result.error(PayMentConstants.ERROR_CODE_ORDER_NOT_EXIST,
                    PayMentConstants.MSG_ORDER_NOT_EXIST);
        }

        // 3. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("payStatus", order.getOrderStatus());
        result.put("payStatusText", getPayStatusText(order));
        result.put("payTime", order.getPayTime());
        result.put("payType", order.getPayType());
        result.put("payTypeText", PayMentConstants.getPayTypeText(order.getPayType()));

        return Result.success(PayMentConstants.SUCCESS_CODE_QUERY_SUCCESS,
                PayMentConstants.SUCCESS_MSG_QUERY_SUCCESS,
                result);
    }

    /**
     * 获取支付状态描述
     */
    private String getPayStatusText(Orders order) {
        Integer orderStatus = order.getOrderStatus();

        // 根据订单状态映射支付状态描述
        if (OrderConstants.ORDER_STATUS_PENDING_PAYMENT.equals(orderStatus)) {
            return PayMentConstants.getPayStatusText(PayMentConstants.PAY_STATUS_WAITING);
        } else if (OrderConstants.ORDER_STATUS_PENDING_DELIVERY.equals(orderStatus) ||
                OrderConstants.ORDER_STATUS_PENDING_RECEIPT.equals(orderStatus) ||
                OrderConstants.ORDER_STATUS_COMPLETED.equals(orderStatus)) {
            return PayMentConstants.getPayStatusText(PayMentConstants.PAY_STATUS_SUCCESS);
        } else if (OrderConstants.ORDER_STATUS_CANCELLED.equals(orderStatus)) {
            return PayMentConstants.getPayStatusText(PayMentConstants.PAY_STATUS_CLOSED);
        }
        return "未知状态";
    }

    /**
     * 支付回调
     */
    @Override
    public Result payNotify(String channel, String notifyData) {
        // 1. 校验渠道合法性
        if(channel == null || channel.isEmpty()){
            return Result.error(PayMentConstants.ERROR_CODE_CHANNEL_NOT_SUPPORT,
                    PayMentConstants.MSG_CHANNEL_NOT_SUPPORT);
        }

        try{
            // TODO: 根据channel解析不同渠道的回调数据
            // 这里简化处理，实际需要：
            // 1. 验证签名
            // 2. 解析订单号
            // 3. 更新订单状态
            // 4. 防止重复回调

            log.info("收到支付回调, channel: {}, data: {}", channel, notifyData);

            // 模拟处理成功
            Map<String, Object> response = new HashMap<>();
            response.put("code", "SUCCESS");
            response.put("message", "回调处理成功");

            return Result.success(PayMentConstants.SUCCESS_CODE_NOTIFY_SUCCESS,
                    PayMentConstants.SUCCESS_MSG_NOTIFY_SUCCESS,
                    response);
        }catch (Exception e){
            log.error(PayMentConstants.MSG_NOTIFY_PROCESS_ERROR, e);
            return Result.error(PayMentConstants.ERROR_CODE_NOTIFY_PROCESS_ERROR,
                    PayMentConstants.MSG_NOTIFY_PROCESS_ERROR);
        }
    }
}