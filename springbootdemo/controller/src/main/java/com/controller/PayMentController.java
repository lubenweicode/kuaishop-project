package com.controller;

import com.service.PayMentService;
import domain.context.UserContext;
import domain.payment.PayMentDTO;
import domain.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PayMentController {

    private final PayMentService payMentService;

    public PayMentController(PayMentService payMentService) {
        this.payMentService = payMentService;
    }

    /**
     * 支付
     *
     * @param payMentDTO
     * @return
     */
    @PostMapping("/pay")
    public Result pay(@RequestBody PayMentDTO payMentDTO){
        Long userId = UserContext.getUserId();
        payMentDTO.setUserId(userId);
        return payMentService.pay(payMentDTO);
    }

    /**
     * 查询支付状态
     *
     * @param orderNo 订单号
     * @return
     */
    @GetMapping("/status")
    public Result getPayStatus(@RequestParam(name = "orderNo",required = true) String orderNo){
        Long userId = UserContext.getUserId();
        return payMentService.getPayStatus(userId, orderNo);
    }

    /**
     * 支付回调
     *
     * @param channel
     * @return
     */
    @PostMapping("/notify/{channel}")
    public Result payNotify(@PathVariable String channel, HttpServletRequest  request){
        String notifyData = readNotifyData(request);
        return payMentService.payNotify(channel, notifyData);
    }

    private String readNotifyData(HttpServletRequest request){
        StringBuilder notifyData = new StringBuilder();
        try {
            String line;
            while ((line = request.getReader().readLine()) != null) {
                notifyData.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifyData.toString();
    }
}
