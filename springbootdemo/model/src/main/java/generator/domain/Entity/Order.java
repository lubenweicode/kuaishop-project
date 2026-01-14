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
* 订单表
* @TableName order
*/
public class Order implements Serializable {

    /**
    * 订单ID
    */
    @NotNull(message="[订单ID]不能为空")
    @ApiModelProperty("订单ID")
    private Long id;
    /**
    * 订单号（QS+年月日+6位随机+2位用户ID取模）
    */
    @NotBlank(message="[订单号（QS+年月日+6位随机+2位用户ID取模）]不能为空")
    @Size(max= 32,message="编码长度不能超过32")
    @ApiModelProperty("订单号（QS+年月日+6位随机+2位用户ID取模）")
    @Length(max= 32,message="编码长度不能超过32")
    private String orderNo;
    /**
    * 用户ID
    */
    @NotNull(message="[用户ID]不能为空")
    @ApiModelProperty("用户ID")
    private Long userId;
    /**
    * 订单总金额
    */
    @NotNull(message="[订单总金额]不能为空")
    @ApiModelProperty("订单总金额")
    private BigDecimal totalAmount;
    /**
    * 优惠金额
    */
    @ApiModelProperty("优惠金额")
    private BigDecimal discountAmount;
    /**
    * 实付金额
    */
    @NotNull(message="[实付金额]不能为空")
    @ApiModelProperty("实付金额")
    private BigDecimal payAmount;
    /**
    * 支付方式：1-微信 2-支付宝
    */
    @ApiModelProperty("支付方式：1-微信 2-支付宝")
    private Integer payType;
    /**
    * 支付时间
    */
    @ApiModelProperty("支付时间")
    private Date payTime;
    /**
    * 状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消
    */
    @ApiModelProperty("状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消")
    private Integer orderStatus;
    /**
    * 发货状态：0-未发货 1-已发货 2-已签收
    */
    @ApiModelProperty("发货状态：0-未发货 1-已发货 2-已签收")
    private Integer deliveryStatus;
    /**
    * 收货人
    */
    @NotBlank(message="[收货人]不能为空")
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("收货人")
    @Length(max= 50,message="编码长度不能超过50")
    private String receiverName;
    /**
    * 收货电话
    */
    @NotBlank(message="[收货电话]不能为空")
    @Size(max= 20,message="编码长度不能超过20")
    @ApiModelProperty("收货电话")
    @Length(max= 20,message="编码长度不能超过20")
    private String receiverPhone;
    /**
    * 收货地址
    */
    @NotBlank(message="[收货地址]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("收货地址")
    @Length(max= 255,message="编码长度不能超过255")
    private String receiverAddress;
    /**
    * 物流公司
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("物流公司")
    @Length(max= 50,message="编码长度不能超过50")
    private String shippingCompany;
    /**
    * 物流单号
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("物流单号")
    @Length(max= 50,message="编码长度不能超过50")
    private String shippingNo;
    /**
    * 订单备注
    */
    @Size(max= 200,message="编码长度不能超过200")
    @ApiModelProperty("订单备注")
    @Length(max= 200,message="编码长度不能超过200")
    private String remark;
    /**
    * 取消原因
    */
    @Size(max= 200,message="编码长度不能超过200")
    @ApiModelProperty("取消原因")
    @Length(max= 200,message="编码长度不能超过200")
    private String cancelReason;
    /**
    * 取消时间
    */
    @ApiModelProperty("取消时间")
    private Date cancelTime;
    /**
    * 完成时间
    */
    @ApiModelProperty("完成时间")
    private Date finishTime;
    /**
    * 下单时间
    */
    @ApiModelProperty("下单时间")
    private Date createTime;
    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
    * 订单ID
    */
    private void setId(Long id){
    this.id = id;
    }

    /**
    * 订单号（QS+年月日+6位随机+2位用户ID取模）
    */
    private void setOrderNo(String orderNo){
    this.orderNo = orderNo;
    }

    /**
    * 用户ID
    */
    private void setUserId(Long userId){
    this.userId = userId;
    }

    /**
    * 订单总金额
    */
    private void setTotalAmount(BigDecimal totalAmount){
    this.totalAmount = totalAmount;
    }

    /**
    * 优惠金额
    */
    private void setDiscountAmount(BigDecimal discountAmount){
    this.discountAmount = discountAmount;
    }

    /**
    * 实付金额
    */
    private void setPayAmount(BigDecimal payAmount){
    this.payAmount = payAmount;
    }

    /**
    * 支付方式：1-微信 2-支付宝
    */
    private void setPayType(Integer payType){
    this.payType = payType;
    }

    /**
    * 支付时间
    */
    private void setPayTime(Date payTime){
    this.payTime = payTime;
    }

    /**
    * 状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消
    */
    private void setOrderStatus(Integer orderStatus){
    this.orderStatus = orderStatus;
    }

    /**
    * 发货状态：0-未发货 1-已发货 2-已签收
    */
    private void setDeliveryStatus(Integer deliveryStatus){
    this.deliveryStatus = deliveryStatus;
    }

    /**
    * 收货人
    */
    private void setReceiverName(String receiverName){
    this.receiverName = receiverName;
    }

    /**
    * 收货电话
    */
    private void setReceiverPhone(String receiverPhone){
    this.receiverPhone = receiverPhone;
    }

    /**
    * 收货地址
    */
    private void setReceiverAddress(String receiverAddress){
    this.receiverAddress = receiverAddress;
    }

    /**
    * 物流公司
    */
    private void setShippingCompany(String shippingCompany){
    this.shippingCompany = shippingCompany;
    }

    /**
    * 物流单号
    */
    private void setShippingNo(String shippingNo){
    this.shippingNo = shippingNo;
    }

    /**
    * 订单备注
    */
    private void setRemark(String remark){
    this.remark = remark;
    }

    /**
    * 取消原因
    */
    private void setCancelReason(String cancelReason){
    this.cancelReason = cancelReason;
    }

    /**
    * 取消时间
    */
    private void setCancelTime(Date cancelTime){
    this.cancelTime = cancelTime;
    }

    /**
    * 完成时间
    */
    private void setFinishTime(Date finishTime){
    this.finishTime = finishTime;
    }

    /**
    * 下单时间
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
    * 订单ID
    */
    private Long getId(){
    return this.id;
    }

    /**
    * 订单号（QS+年月日+6位随机+2位用户ID取模）
    */
    private String getOrderNo(){
    return this.orderNo;
    }

    /**
    * 用户ID
    */
    private Long getUserId(){
    return this.userId;
    }

    /**
    * 订单总金额
    */
    private BigDecimal getTotalAmount(){
    return this.totalAmount;
    }

    /**
    * 优惠金额
    */
    private BigDecimal getDiscountAmount(){
    return this.discountAmount;
    }

    /**
    * 实付金额
    */
    private BigDecimal getPayAmount(){
    return this.payAmount;
    }

    /**
    * 支付方式：1-微信 2-支付宝
    */
    private Integer getPayType(){
    return this.payType;
    }

    /**
    * 支付时间
    */
    private Date getPayTime(){
    return this.payTime;
    }

    /**
    * 状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消
    */
    private Integer getOrderStatus(){
    return this.orderStatus;
    }

    /**
    * 发货状态：0-未发货 1-已发货 2-已签收
    */
    private Integer getDeliveryStatus(){
    return this.deliveryStatus;
    }

    /**
    * 收货人
    */
    private String getReceiverName(){
    return this.receiverName;
    }

    /**
    * 收货电话
    */
    private String getReceiverPhone(){
    return this.receiverPhone;
    }

    /**
    * 收货地址
    */
    private String getReceiverAddress(){
    return this.receiverAddress;
    }

    /**
    * 物流公司
    */
    private String getShippingCompany(){
    return this.shippingCompany;
    }

    /**
    * 物流单号
    */
    private String getShippingNo(){
    return this.shippingNo;
    }

    /**
    * 订单备注
    */
    private String getRemark(){
    return this.remark;
    }

    /**
    * 取消原因
    */
    private String getCancelReason(){
    return this.cancelReason;
    }

    /**
    * 取消时间
    */
    private Date getCancelTime(){
    return this.cancelTime;
    }

    /**
    * 完成时间
    */
    private Date getFinishTime(){
    return this.finishTime;
    }

    /**
    * 下单时间
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
