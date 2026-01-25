package generator.domain.order;

import lombok.Data;

@Data
public class OrderItem {
    private Long productId; // 商品ID
    private Integer quantity; // 购买数量

    public OrderItem(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderItem() {
    }

    public String toString() {
        return "OrderItem(productId=" + this.getProductId() + ", quantity=" + this.getQuantity() + ")";
    }
}
