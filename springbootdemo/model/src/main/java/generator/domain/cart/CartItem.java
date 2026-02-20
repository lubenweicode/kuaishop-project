package generator.domain.cart;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CartItem {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer selected;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
