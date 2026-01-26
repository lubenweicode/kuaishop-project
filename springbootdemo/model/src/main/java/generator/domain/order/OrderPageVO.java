package generator.domain.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderPageVO {

    private Long total; // 总记录数
    private Long pages; // 总页数
    private Integer size; // 页大小
    private Integer current; // 当前页码
    private List<OrderVO> records; // 订单列表

}
