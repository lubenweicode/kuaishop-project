package generator.domain.cart;

import lombok.Data;

import java.util.Date;

@Data
public class CartItem {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer selected;
    private Date createTime;
    private Date updateTime;
}
