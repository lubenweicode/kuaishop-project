package generator.domain.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginAuthDTO {

    // 用户名：必填，3-20字符（字母、数字、下划线）
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名需为3-20位字母、数字或下划线")
    private String username;

    // 密码：必填，6-20字符（强校验：必须包含字母+数字）
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{6,20}$", message = "密码需为6-20位，且包含字母和数字")
    private String password;

}
