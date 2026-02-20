package generator.domain.product;

import lombok.Data;

@Data
public class ProductPageDTO {

    private Integer page = 5; // 默认每页5条数据
    private Integer size = 12; // 默认每页12条数据
    private String keyword; // 搜索关键字
    private Integer categoryId; // 分类ID
    private String sortBy; // 排序字段 price\sales\createTime
    private String order; // 排序方式 asc\desc
}
