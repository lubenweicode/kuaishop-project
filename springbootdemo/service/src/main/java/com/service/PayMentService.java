package com.service;

import generator.domain.payment.PayMentDTO;
import generator.domain.response.Result;

public interface PayMentService {
    Result pay(PayMentDTO payMentDTO);

    Result getPayStatus(Long userId, String orderNo);

    Result payNotify(String channel,String notifyData);
}
