package domain.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterAuthVO {

    private Long id;
    private String username;
    private String phone;
    private LocalDateTime createTime;
}
