package generator.domain.Entity;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
* 购物车表
* @TableName cart
*/
public class Cart implements Serializable {

    /**
    * 购物车项ID
    */
    @NotNull(message="[购物车项ID]不能为空")
    @ApiModelProperty("购物车项ID")
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

    /**
    * 购物车项ID
    */
    private void setId(Long id){
    this.id = id;
    }

    /**
    * 用户ID
    */
    private void setUserId(Long userId){
    this.userId = userId;
    }

    /**
    * 商品ID
    */
    private void setProductId(Long productId){
    this.productId = productId;
    }

    /**
    * 数量
    */
    private void setQuantity(Integer quantity){
    this.quantity = quantity;
    }

    /**
    * 是否选中
    */
    private void setSelected(Integer selected){
    this.selected = selected;
    }

    /**
    * 加入时间
    */
    private void setCreateTime(Date createTime){
    this.createTime = createTime;
    }

    /**
    * 更新时间
    */
    private void setUpdateTime(Date updateTime){
    this.updateTime = updateTime;
    }


    /**
    * 购物车项ID
    */
    private Long getId(){
    return this.id;
    }

    /**
    * 用户ID
    */
    private Long getUserId(){
    return this.userId;
    }

    /**
    * 商品ID
    */
    private Long getProductId(){
    return this.productId;
    }

    /**
    * 数量
    */
    private Integer getQuantity(){
    return this.quantity;
    }

    /**
    * 是否选中
    */
    private Integer getSelected(){
    return this.selected;
    }

    /**
    * 加入时间
    */
    private Date getCreateTime(){
    return this.createTime;
    }

    /**
    * 更新时间
    */
    private Date getUpdateTime(){
    return this.updateTime;
    }

}
