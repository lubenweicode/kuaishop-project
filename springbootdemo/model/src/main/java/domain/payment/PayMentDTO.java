package domain.payment;

import lombok.Data;

@Data
public class PayMentDTO {

    private String orderNo; // 订单编号
    private Integer payType; // 支付方式 1-微信 2-支付宝
    private Long userId;
}
