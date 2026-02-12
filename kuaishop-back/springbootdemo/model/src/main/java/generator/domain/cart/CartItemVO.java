package generator.domain.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CartItemVO {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String name;
    private String mainImage;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String categoryName;
    private String categoryImage;
}
