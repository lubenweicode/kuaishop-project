package generator.domain.order;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVO {

    private String orderNo; // 订单号
    private Long userId; // 用户ID
    private String username; // 用户名
    private BigDecimal totalAmount; // 订单总金额
    private BigDecimal payAmount; // 实付金额
    private Integer status; // 订单状态
    private String statusText; // 订单状态文本
    private String info; // 订单信息
    private String remark; // 订单备注
    private String receiverName; // 收货人
    private String receiverPhone; // 收货人电话
    private String receiverAddress; // 收货人地址
    private Date createTime; // 创建时间
    private String payUrl; // 支付链接
    private Date paymentTime; // 支付时间
    private List<OrderItemVO> items; // 订单商品列表
    private List<String> logistics; // 物流信息

}
