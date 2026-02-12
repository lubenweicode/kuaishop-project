package generator.domain.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class Order {

    private Integer id; // 订单ID
    private String orderNo; // 订单编号
    private Integer status; // 订单状态
    private String statusText; // 订单状态文本
    private LocalDateTime createTime; // 创建时间
    private List<item> items; // 订单项
}

@Data
class item {
    private Integer id; // 订单项ID
    private Integer orderId; // 订单ID
    private Integer productId; // 商品ID
    private String productName; // 商品名称
    private String productImage; // 商品图片
    private BigDecimal productPrice; // 商品价格
    private Integer quantity; // 商品数量
    private BigDecimal totalPrice; // 商品总价
}
