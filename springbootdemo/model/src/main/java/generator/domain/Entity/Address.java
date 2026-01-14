package generator.domain.Entity;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * 收货地址表
 * 
 * @TableName address
 */
public class Address implements Serializable {

    /**
     * 地址ID
     */
    @NotNull(message = "[地址ID]不能为空")
    @ApiModelProperty("地址ID")
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
    @Length(max = 255, message = "编码长度不能超过255")
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

    /**
     * 地址ID
     */
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * 用户ID
     */
    private void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 收货人
     */
    private void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    /**
     * 收货电话
     */
    private void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    /**
     * 省份
     */
    private void setProvince(String province) {
        this.province = province;
    }

    /**
     * 城市
     */
    private void setCity(String city) {
        this.city = city;
    }

    /**
     * 区县
     */
    private void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 详细地址
     */
    private void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    /**
     * 邮政编码
     */
    private void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * 是否默认地址
     */
    private void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * 状态：0-删除 1-正常
     */
    private void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 创建时间
     */
    private void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新时间
     */
    private void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 地址ID
     */
    private Long getId() {
        return this.id;
    }

    /**
     * 用户ID
     */
    private Long getUserId() {
        return this.userId;
    }

    /**
     * 收货人
     */
    private String getReceiverName() {
        return this.receiverName;
    }

    /**
     * 收货电话
     */
    private String getReceiverPhone() {
        return this.receiverPhone;
    }

    /**
     * 省份
     */
    private String getProvince() {
        return this.province;
    }

    /**
     * 城市
     */
    private String getCity() {
        return this.city;
    }

    /**
     * 区县
     */
    private String getDistrict() {
        return this.district;
    }

    /**
     * 详细地址
     */
    private String getDetailAddress() {
        return this.detailAddress;
    }

    /**
     * 邮政编码
     */
    private String getPostalCode() {
        return this.postalCode;
    }

    /**
     * 是否默认地址
     */
    private Integer getIsDefault() {
        return this.isDefault;
    }

    /**
     * 状态：0-删除 1-正常
     */
    private Integer getStatus() {
        return this.status;
    }

    /**
     * 创建时间
     */
    private Date getCreateTime() {
        return this.createTime;
    }

    /**
     * 更新时间
     */
    private Date getUpdateTime() {
        return this.updateTime;
    }

}
