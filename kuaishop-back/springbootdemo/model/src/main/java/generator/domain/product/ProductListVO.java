package generator.domain.product;

import generator.domain.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductListVO {

    private Long total; // 总记录数
    private Integer page; // 当前页码
    private Integer size; // 每页记录数
    private Integer current; // 总页数
    private List<Product> records; // 当前页数据
}
