package generator.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 购物车表
 *
 * @TableName cart
 */
@Data
public class Cart implements Serializable {

    /**
     * 购物车项ID
     */
    @ApiModelProperty("购物车项ID")
    @NotNull(message = "[购物车项ID]不能为空")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @NotNull(message = "[用户ID]不能为空")
    @TableField(value = "user_id")
    private Long userId;
    /**
     * 商品ID
     */
    @ApiModelProperty("商品ID")
    @NotNull(message = "[商品ID]不能为空")
    @TableField(value = "product_id")
    private Long productId;
    /**
     * 数量
     */
    @ApiModelProperty("数量")
    @NotNull(message = "[数量]不能为空")
    @TableField(value = "quantity")
    private Integer quantity;
    /**
     * 是否选中
     */
    @ApiModelProperty("是否选中")
    @TableField(value = "selected")
    private Integer selected;
    /**
     * 加入时间
     */
    @ApiModelProperty("加入时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField(value = "update_time")
    private Date updateTime;

}
