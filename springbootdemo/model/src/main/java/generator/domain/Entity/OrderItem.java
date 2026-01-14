package generator.domain.Entity;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* 订单明细表
* @TableName order_item
*/
public class OrderItem implements Serializable {

    /**
    * 订单项ID
    */
    @NotNull(message="[订单项ID]不能为空")
    @ApiModelProperty("订单项ID")
    private Long id;
    /**
    * 订单ID
    */
    @NotNull(message="[订单ID]不能为空")
    @ApiModelProperty("订单ID")
    private Long orderId;
    /**
    * 订单号（冗余）
    */
    @NotBlank(message="[订单号（冗余）]不能为空")
    @Size(max= 32,message="编码长度不能超过32")
    @ApiModelProperty("订单号（冗余）")
    @Length(max= 32,message="编码长度不能超过32")
    private String orderNo;
    /**
    * 商品ID
    */
    @NotNull(message="[商品ID]不能为空")
    @ApiModelProperty("商品ID")
    private Long productId;
    /**
    * 商品名称
    */
    @NotBlank(message="[商品名称]不能为空")
    @Size(max= 200,message="编码长度不能超过200")
    @ApiModelProperty("商品名称")
    @Length(max= 200,message="编码长度不能超过200")
    private String productName;
    /**
    * 商品图片
    */
    @NotBlank(message="[商品图片]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("商品图片")
    @Length(max= 255,message="编码长度不能超过255")
    private String productImage;
    /**
    * 购买时价格
    */
    @NotNull(message="[购买时价格]不能为空")
    @ApiModelProperty("购买时价格")
    private BigDecimal productPrice;
    /**
    * 规格信息
    */
    @Size(max= 200,message="编码长度不能超过200")
    @ApiModelProperty("规格信息")
    @Length(max= 200,message="编码长度不能超过200")
    private String specInfo;
    /**
    * 购买数量
    */
    @NotNull(message="[购买数量]不能为空")
    @ApiModelProperty("购买数量")
    private Integer quantity;
    /**
    * 小计金额
    */
    @NotNull(message="[小计金额]不能为空")
    @ApiModelProperty("小计金额")
    private BigDecimal totalPrice;
    /**
    * 退款状态：0-无退款 1-退款中 2-退款成功
    */
    @ApiModelProperty("退款状态：0-无退款 1-退款中 2-退款成功")
    private Integer refundStatus;
    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
    * 订单项ID
    */
    private void setId(Long id){
    this.id = id;
    }

    /**
    * 订单ID
    */
    private void setOrderId(Long orderId){
    this.orderId = orderId;
    }

    /**
    * 订单号（冗余）
    */
    private void setOrderNo(String orderNo){
    this.orderNo = orderNo;
    }

    /**
    * 商品ID
    */
    private void setProductId(Long productId){
    this.productId = productId;
    }

    /**
    * 商品名称
    */
    private void setProductName(String productName){
    this.productName = productName;
    }

    /**
    * 商品图片
    */
    private void setProductImage(String productImage){
    this.productImage = productImage;
    }

    /**
    * 购买时价格
    */
    private void setProductPrice(BigDecimal productPrice){
    this.productPrice = productPrice;
    }

    /**
    * 规格信息
    */
    private void setSpecInfo(String specInfo){
    this.specInfo = specInfo;
    }

    /**
    * 购买数量
    */
    private void setQuantity(Integer quantity){
    this.quantity = quantity;
    }

    /**
    * 小计金额
    */
    private void setTotalPrice(BigDecimal totalPrice){
    this.totalPrice = totalPrice;
    }

    /**
    * 退款状态：0-无退款 1-退款中 2-退款成功
    */
    private void setRefundStatus(Integer refundStatus){
    this.refundStatus = refundStatus;
    }

    /**
    * 创建时间
    */
    private void setCreateTime(Date createTime){
    this.createTime = createTime;
    }


    /**
    * 订单项ID
    */
    private Long getId(){
    return this.id;
    }

    /**
    * 订单ID
    */
    private Long getOrderId(){
    return this.orderId;
    }

    /**
    * 订单号（冗余）
    */
    private String getOrderNo(){
    return this.orderNo;
    }

    /**
    * 商品ID
    */
    private Long getProductId(){
    return this.productId;
    }

    /**
    * 商品名称
    */
    private String getProductName(){
    return this.productName;
    }

    /**
    * 商品图片
    */
    private String getProductImage(){
    return this.productImage;
    }

    /**
    * 购买时价格
    */
    private BigDecimal getProductPrice(){
    return this.productPrice;
    }

    /**
    * 规格信息
    */
    private String getSpecInfo(){
    return this.specInfo;
    }

    /**
    * 购买数量
    */
    private Integer getQuantity(){
    return this.quantity;
    }

    /**
    * 小计金额
    */
    private BigDecimal getTotalPrice(){
    return this.totalPrice;
    }

    /**
    * 退款状态：0-无退款 1-退款中 2-退款成功
    */
    private Integer getRefundStatus(){
    return this.refundStatus;
    }

    /**
    * 创建时间
    */
    private Date getCreateTime(){
    return this.createTime;
    }

}
