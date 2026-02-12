package generator.domain.seckill;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class SeckillActivityDTO {

    private Long id; // 主键
    private String activityName; // 活动名称
    private Date startTime; // 开始时间
    private Date endTime; // 结束时间
    private Integer status; // 状态
    private List<SeckillProductDTO> products; // 秒杀商品
    private Date createTime; // 创建时间
    private Date updateTime; // 修改时间

}
