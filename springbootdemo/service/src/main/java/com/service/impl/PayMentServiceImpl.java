package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.constant.OrderConstants;
import com.repository.mapper.OrderMapper;
import com.service.PayMentService;
import generator.domain.entity.Orders;
import generator.domain.payment.PayMentDTO;
import generator.domain.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

import static com.constant.OrderConstants.*;

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
            return Result.error(500, MSG_USER_ID_REQUIRED);
        }

        // 1.2 订单号不能为空
        if(payMentDTO.getOrderNo() == null){
            return Result.error(500, MSG_ORDER_NO_REQUIRED);
        }

        // 1.3 支付方式不能为空
        if(payMentDTO.getPayType() == null){
            return Result.error(500, MSG_PAY_TYPE_NOT_NULL);
        }else if(payMentDTO.getPayType() != 1 && payMentDTO.getPayType() != 2){
            return Result.error(500, MSG_PAY_TYPE_ERROR);
        }

        // 2. 订单校验
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, payMentDTO.getUserId());
        queryWrapper.eq(Orders::getOrderNo, payMentDTO.getOrderNo());
        Orders order = orderMapper.selectOne(queryWrapper);

        // 2.1 订单不存在
        if (order == null) {
            log.warn("订单不存在");
            return Result.error(404, MSG_ORDER_NOT_EXIST);
        }

        // 2.2 订单状态校验
        if (!Objects.equals(order.getOrderStatus(), OrderConstants.ORDER_STATUS_PENDING_PAYMENT)) {
            log.warn("订单状态错误,当前状态:{}", order.getOrderStatus());
            return Result.error(400, MSG_ORDER_STATUS_ERROR);
        }

        // 2.3 修改订单状态
        order.setOrderStatus(OrderConstants.ORDER_STATUS_PENDING_DELIVERY);
        order.setPayType(payMentDTO.getPayType());
        String tradeNo = "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0,20);
        String tradeSign = generateSign(order.getOrderNo(),tradeNo);
        order.setOutTradeNo(tradeNo);
        order.setPaySecret(tradeSign);
        order.setUpdateTime(new Date());
        order.setOrderStatus(1);
        int update = orderMapper.updateById(order);
        if(update != 1) {
            log.warn("订单{}支付失败", order.getOrderNo());
            return Result.error(500, MSG_PAY_NOTIFY_FAILED);
        }

        String payUrl = null; // 支付链接
        String qrlCode = null; // 二维码
        // 3. 返回
        // 3.1 微信
        if(payMentDTO.getPayType() == 1){
            payUrl = "weixin://wxpay/bizpayurl?pr=xxx";
            qrlCode = "https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=5&index=6";
        }
        // 3.2 支付宝
        if(payMentDTO.getPayType() == 2){
            payUrl = "zhifubao://zhifubao/bizpayurl?pr=xxx";
            qrlCode = "https://docs.open.alipay.com/204/105465/";
        }


        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("payAmount", order.getPayAmount());
        result.put("payUrl", payUrl);
        result.put("qrlCode", qrlCode);
        return Result.success(result);
    }

    /**
     * 生成签名
     * @param orderNo
     * @param tradeNo
     * @return
     */
    private String generateSign(String orderNo, String tradeNo) {
        String signStr = orderNo + tradeNo + PAY_PLATFORM_SECRET;
        return DigestUtils.md5DigestAsHex(signStr.getBytes());
    }

    /**
     * 查询支付状态
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public Result getPayStatus(Long userId, String orderNo) {

        // 1. 参数校验
        if(userId == null){
            return Result.error(500, MSG_USER_ID_REQUIRED);
        }

        // 1.1 订单号不能为空
        if(orderNo == null){
            return Result.error(500, MSG_ORDER_NO_REQUIRED);
        }

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        queryWrapper.eq(Orders::getOrderNo, orderNo);

        Orders order = orderMapper.selectOne(queryWrapper);

        if (order == null) {
            log.warn( MSG_ORDER_NOT_EXIST);
            return Result.error(404,  MSG_ORDER_NOT_EXIST);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("payStatus", order.getOrderStatus());
        result.put("payTime", order.getPayTime());
        return Result.success(result);
    }

    /**
     * 支付回调
     * @param channel
     * @param notifyData
     * @return
     */
    @Override
    public Result payNotify(String channel, String notifyData) {
        // 1. 校验渠道合法性
        if(channel == null || channel.isEmpty()){
            return Result.error(500, "渠道不能为空");
        }

        try{
            // 2. 解析回调数据
            String response = "123456";
            return Result.success(response);
        }catch (Exception e){
            log.error(MSG_PAY_NOTIFY_ERROR, e);
            return Result.error(500, MSG_PAY_NOTIFY_ERROR);
        }
    }


}
