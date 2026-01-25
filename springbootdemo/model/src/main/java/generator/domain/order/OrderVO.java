package generator.domain.order;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderVO {

    private String orderNo; // 订单号
    private BigDecimal totalAmount; // 订单总金额
    private BigDecimal payAmount; // 实付金额
    private Long orderId; // 订单ID
    private String payUrl; // 支付链接
}
