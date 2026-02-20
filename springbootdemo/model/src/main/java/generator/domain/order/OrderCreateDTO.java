package generator.domain.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {

    private List<Long> cartItemIds;// 购物车项ID
    private List<OrderItem> items; // 订单项
    private String addressId; // 收货地址ID
    private String couponId; // 优惠券ID
    private String remark; // 订单备注

}
