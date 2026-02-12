package generator.domain.seckill;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@JSONType(orders = {"id", "activityName", "status", "statusText", "startTime", "endTime"})
@Data
public class SeckillActivityCacheDTO {

    private Long id;
    private String activityName;
    private Integer status;
    private String statusText;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
