package generator.domain.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车添加商品参数
 */
@Data
public class CartAddItem {

    @NotNull(message = "商品ID不能为空")
    private Integer productId;
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量不能小于1")
    private Integer quantity;

}
