package com.service;

import domain.payment.PayMentDTO;
import domain.response.Result;

public interface PayMentService {
    Result pay(PayMentDTO payMentDTO);

    Result getPayStatus(Long userId, String orderNo);

    Result payNotify(String channel,String notifyData);
}
