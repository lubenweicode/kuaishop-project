package generator.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 收货地址表
 *
 * @TableName address
 */
@Data
public class Address implements Serializable {

    /**
     * 地址ID
     */
    @ApiModelProperty("地址ID")
    @NotNull(message = "[地址ID]不能为空")
    @Size(max = 20, message = "编码长度不能超过20")
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
     * 收货人
     */
    @ApiModelProperty("收货人")
    @NotBlank(message = "[收货人]不能为空")
    @Length(max = 50, message = "编码长度不能超过50")
    @TableField(value = "receiver_name")
    private String receiverName;
    /**
     * 收货电话
     */
    @ApiModelProperty("收货电话")
    @NotBlank(message = "[收货电话]不能为空")
    @Length(max = 20, message = "编码长度不能超过20")
    @TableField(value = "receiver_phone")
    private String receiverPhone;
    /**
     * 省份
     */
    @ApiModelProperty("省份")
    @NotBlank(message = "[省份]不能为空")
    @Length(max = 50, message = "编码长度不能超过50")
    @TableField(value = "province")
    private String province;
    /**
     * 城市
     */
    @ApiModelProperty("城市")
    @NotBlank(message = "[城市]不能为空")
    @Length(max = 50, message = "编码长度不能超过50")
    @TableField(value = "city")
    private String city;
    /**
     * 区县
     */
    @ApiModelProperty("区县")
    @NotBlank(message = "[区县]不能为空")
    @Length(max = 50, message = "编码长度不能超过50")
    @TableField(value = "district")
    private String district;
    /**
     * 详细地址
     */
    @ApiModelProperty("详细地址")
    @Length(max = 200, message = "编码长度不能超过200")
    @NotBlank(message = "[详细地址]不能为空")
    @TableField(value = "detail_address")
    private String detailAddress;
    /**
     * 邮政编码
     */
    @ApiModelProperty("邮政编码")
    @Length(max = 10, message = "编码长度不能超过10")
    @TableField(value = "postal_code")
    private String postalCode;
    /**
     * 是否默认地址
     */
    @ApiModelProperty("是否默认地址")
    @TableField(value = "is_default")
    private Integer isDefault;
    /**
     * 状态：0-删除 1-正常
     */
    @ApiModelProperty("状态：0-删除 1-正常")
    @TableField(value = "status")
    private Integer status;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
