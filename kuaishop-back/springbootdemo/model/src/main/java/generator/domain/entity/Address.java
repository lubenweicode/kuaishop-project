package generator.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
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
    @NotNull(message = "[地址ID]不能为空")
    @ApiModelProperty("地址ID")
    @Size(max = 20, message = "编码长度不能超过20")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    @NotNull(message = "[用户ID]不能为空")
    @ApiModelProperty("用户ID")
    private Long userId;
    /**
     * 收货人
     */
    @NotBlank(message = "[收货人]不能为空")
    @Size(max = 50, message = "编码长度不能超过50")
    @ApiModelProperty("收货人")
    @Length(max = 50, message = "编码长度不能超过50")
    private String receiverName;
    /**
     * 收货电话
     */
    @NotBlank(message = "[收货电话]不能为空")
    @Size(max = 20, message = "编码长度不能超过20")
    @ApiModelProperty("收货电话")
    @Length(max = 20, message = "编码长度不能超过20")
    private String receiverPhone;
    /**
     * 省份
     */
    @NotBlank(message = "[省份]不能为空")
    @Size(max = 50, message = "编码长度不能超过50")
    @ApiModelProperty("省份")
    @Length(max = 50, message = "编码长度不能超过50")
    private String province;
    /**
     * 城市
     */
    @NotBlank(message = "[城市]不能为空")
    @Size(max = 50, message = "编码长度不能超过50")
    @ApiModelProperty("城市")
    @Length(max = 50, message = "编码长度不能超过50")
    private String city;
    /**
     * 区县
     */
    @NotBlank(message = "[区县]不能为空")
    @Size(max = 50, message = "编码长度不能超过50")
    @ApiModelProperty("区县")
    @Length(max = 50, message = "编码长度不能超过50")
    private String district;
    /**
     * 详细地址
     */
    @NotBlank(message = "[详细地址]不能为空")
    @Size(max = 255, message = "编码长度不能超过255")
    @ApiModelProperty("详细地址")
    private String detailAddress;
    /**
     * 邮政编码
     */
    @Size(max = 10, message = "编码长度不能超过10")
    @ApiModelProperty("邮政编码")
    @Length(max = 10, message = "编码长度不能超过10")
    private String postalCode;
    /**
     * 是否默认地址
     */
    @ApiModelProperty("是否默认地址")
    private Integer isDefault;
    /**
     * 状态：0-删除 1-正常
     */
    @ApiModelProperty("状态：0-删除 1-正常")
    private Integer status;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
