package generator.domain.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private Long productId; // 商品ID
    private String productName; // 商品名称
    private String productImage; // 商品图片
    private BigDecimal price; // 商品价格
    private Integer quantity; // 商品数量
    private BigDecimal totalPrice; // 商品总价
}
