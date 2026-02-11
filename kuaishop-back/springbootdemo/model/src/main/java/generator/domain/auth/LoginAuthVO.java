package generator.domain.auth;

import lombok.Data;

@Data
public class LoginAuthVO {

    private String token;
    private String username;
    private String phone;
    private String avatar;
}
