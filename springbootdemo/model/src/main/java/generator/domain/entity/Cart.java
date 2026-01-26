package generator.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 购物车表
* @TableName cart
*/
@Data
public class Cart implements Serializable {

    /**
    * 购物车项ID
    */
    @NotNull(message="[购物车项ID]不能为空")
    @ApiModelProperty("购物车项ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
    * 用户ID
    */
    @NotNull(message="[用户ID]不能为空")
    @ApiModelProperty("用户ID")
    private Long userId;
    /**
    * 商品ID
    */
    @NotNull(message="[商品ID]不能为空")
    @ApiModelProperty("商品ID")
    private Long productId;
    /**
    * 数量
    */
    @ApiModelProperty("数量")
    private Integer quantity;
    /**
    * 是否选中
    */
    @ApiModelProperty("是否选中")
    private Integer selected;
    /**
    * 加入时间
    */
    @ApiModelProperty("加入时间")
    private Date createTime;
    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    private Date updateTime;

}
