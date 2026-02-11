package generator.domain.address;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDTO {

    @NotBlank(message = "收货人不能为空")
    private String receiverName; // 收货人
    @NotBlank(message = "收货电话不能为空")
    private String receiverPhone; // 收货电话
    @NotBlank(message = "省份不能为空")
    private String province; // 省份
    @NotBlank(message = "城市不能为空")
    private String city; // 城市
    @NotBlank(message = "区县不能为空")
    private String district; // 区县
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress; // 详细地址
    private Boolean isDefault; // 是否默认
}
