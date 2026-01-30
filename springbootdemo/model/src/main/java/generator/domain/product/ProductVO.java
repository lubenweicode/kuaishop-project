package generator.domain.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductVO {

    private String name; // 商品名称
    private String description; // 商品描述
    private String detail; // 商品详情
    private BigDecimal price; // 商品价格
    private BigDecimal originalPrice; // 商品原价
    private Integer stock; // 商品库存
    private Long categoryId; // 商品分类ID
    private List<String> images; // 商品图片
    private List<Map<String,String>> specifications; // 商品规格
    private Integer status; // 商品状态
}
