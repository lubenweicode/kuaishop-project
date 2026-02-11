package generator.domain.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductDTO {

    @NotBlank(message = "商品名称不能为空")
    @NotNull(message = "商品名称不能为空")
    private String name; // 商品名称

    @NotBlank(message = "商品描述不能为空")
    @NotNull(message = "商品描述不能为空")
    private String description; // 商品描述

    private String detail; // 商品详情

    @NotNull(message = "商品价格不能为空")
    private BigDecimal price; // 商品价格


    private BigDecimal originalPrice; // 商品原价

    @NotNull(message = "商品库存不能为空")
    private Integer stock; // 商品库存

    @NotNull(message = "商品分类ID不能为空")
    private Long categoryId; // 商品分类ID

    @NotNull(message = "商品图片不能为空")
    private List<String> images; // 商品图片

    @NotNull(message = "商品规格不能为空")
    private List<Map<String, String>> specifications; // 商品规格
    private Integer status = 1; // 商品状态
}
