package generator.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 订单表
 * @TableName orders
 */
@TableName(value ="orders")
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
    @Size(max= 32,message="编码长度不能超过32")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @ApiModelProperty("用户ID")
    @Size(max= 32,message="编码长度不能超过32")
    private Long userId;

    /**
     * 实付金额
     */
    @TableField(value = "pay_amount")
    @ApiModelProperty("实付金额")
    @Size(max= 10,message="编码长度不能超过10")
    private BigDecimal payAmount;

    /**
     * 总金额
     */
    @TableField(value = "total_amount",exist = false)
    @ApiModelProperty("总金额")
    @Size(max= 10,message="编码长度不能超过10")
    private BigDecimal totalAmount;

    /**
     * 状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消
     */
    @TableField(value = "order_status")
    @ApiModelProperty("状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消")
    @Size(max= 1,message="编码长度不能超过1")
    private Integer orderStatus;

    /**
     * 原因
     */
    @TableField(value = "reason")
    @ApiModelProperty("原因")
    @Size(max= 200,message="编码长度不能超过200")
    private String reason;

    /**
     * 订单详情：数量、类型等
     */
    @TableField(value = "info")
    @ApiModelProperty("订单详情：数量、类型等")
    @Size(max= 200,message="编码长度不能超过200")
    private String info;

    /**
     * 下单时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty("下单时间")
    @Size(max= 19,message="编码长度不能超过19")
    private Date createTime;

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
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", userId=").append(userId);
        sb.append(", payAmount=").append(payAmount);
        sb.append(", orderStatus=").append(orderStatus);
        sb.append(", info=").append(info);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }

    public void setTotalAmount(BigDecimal totalAmount) {
    }


}