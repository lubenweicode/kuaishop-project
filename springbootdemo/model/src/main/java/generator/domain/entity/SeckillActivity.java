package generator.domain.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀活动表
 * @TableName seckill_activity
 */
@TableName(value ="seckill_activity")
@Data
public class SeckillActivity implements Serializable {
    /**
     * 秒杀活动ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称
     */
    @ApiModelProperty("活动名称")
    @TableField(value = "activity_name")
    private String activityName;

    /**
     * 活动开始时间
     */
    @ApiModelProperty("活动开始时间")
    @TableField(value = "start_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 活动结束时间
     */
    @ApiModelProperty("活动结束时间")
    @TableField(value = "end_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 活动状态：0-未开始 1-进行中 2-已结束
     */
    @ApiModelProperty("活动状态：0-未开始 1-进行中 2-已结束")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty("活动状态文本")
    @TableField(value = "status_text", exist = false)
    private String statusText;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}