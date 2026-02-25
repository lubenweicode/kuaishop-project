package generator.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表
 *
 * @TableName orders
 */
@TableName(value = "orders")
@Data
public class Orders {
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField(value = "order_no")
    @ApiModelProperty("订单号")
    @Length(max = 32, message = "编码长度不能超过32")
    private String orderNo;

    /**
     * 活动ID
     */
    @ApiModelProperty("活动ID")
    @Length(max = 32, message = "编码长度不能超过32")
    @TableField(value = "activity_id", exist = false)
    private Long activityId;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @Length(max = 32, message = "编码长度不能超过32")
    @TableField(value = "user_id")
    private Long userId;


    /**
     * 实付金额
     */
    @ApiModelProperty("实付金额")
    @Length(max = 10, message = "编码长度不能超过10")
    @TableField(value = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 总金额
     */
    @ApiModelProperty("总金额")
    @Length(max = 10, message = "编码长度不能超过10")
    @TableField(value = "total_amount", exist = false)
    private BigDecimal totalAmount;

    /**
     * 状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消
     */

    @ApiModelProperty("状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消")
    @Length(max = 1, message = "编码长度不能超过1")
    @TableField(value = "order_status")
    private Integer orderStatus;

    /**
     * 原因
     */
    @ApiModelProperty("原因")
    @Length(max = 200, message = "编码长度不能超过200")
    @TableField(value = "reason")
    private String reason;

    /**
     * 订单详情：数量、类型等
     */

    @ApiModelProperty("订单详情：数量、类型等")
    @Length(max = 200, message = "编码长度不能超过200")
    @TableField(value = "info")
    private String info;

    /**
     * 物流公司：
     */
    @ApiModelProperty("物流公司：")
    @Length(max = 200, message = "编码长度不能超过200")
    @TableField(value = "logistics_company")
    private String logisticsCompany;

    /**
     * 物流单号
     */
    @ApiModelProperty("物流单号")
    @Length(max = 200, message = "编码长度不能超过200")
    @TableField(value = "logistics_no")
    private String logisticsNo;

    /**
     * 支付平台订单号
     */
    @ApiModelProperty("支付平台订单号")
    @Length(max = 200, message = "编码长度不能超过200")
    @TableField(value = "out_trade_no")
    private String outTradeNo;

    /**
     * 支付方式：1-支付宝 2-微信 3-货到付款
     */
    @ApiModelProperty("支付方式：1-支付宝 2-微信 3-货到付款")
    @Length(max = 1, message = "编码长度不能超过1")
    @TableField(value = "pay_type")
    private Integer payType;

    /**
     * 支付密钥
     */
    @ApiModelProperty("支付密钥")
    @TableField(value = "pay_secret")
    private String paySecret;

    /**
     * 支付时间
     */
    @ApiModelProperty("支付时间")
    @Length(max = 19, message = "编码长度不能超过19")
    @TableField(value = "pay_time")
    private Date payTime;


    /**
     * 下单时间
     */
    @ApiModelProperty("下单时间")
    @Length(max = 19, message = "编码长度不能超过19")
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @Length(max = 19, message = "编码长度不能超过19")
    @TableField(value = "update_time")
    private Date updateTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Orders other = (Orders) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getOrderNo() == null ? other.getOrderNo() == null : this.getOrderNo().equals(other.getOrderNo()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getPayAmount() == null ? other.getPayAmount() == null : this.getPayAmount().equals(other.getPayAmount()))
                && (this.getOrderStatus() == null ? other.getOrderStatus() == null : this.getOrderStatus().equals(other.getOrderStatus()))
                && (this.getInfo() == null ? other.getInfo() == null : this.getInfo().equals(other.getInfo()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNo() == null) ? 0 : getOrderNo().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getPayAmount() == null) ? 0 : getPayAmount().hashCode());
        result = prime * result + ((getOrderStatus() == null) ? 0 : getOrderStatus().hashCode());
        result = prime * result + ((getInfo() == null) ? 0 : getInfo().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        String sb = getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", orderNo=" + orderNo +
                ", userId=" + userId +
                ", payAmount=" + payAmount +
                ", orderStatus=" + orderStatus +
                ", info=" + info +
                ", createTime=" + createTime +
                "]";
        return sb;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
    }


    public void setUpdateTime(Date date) {
    }


}