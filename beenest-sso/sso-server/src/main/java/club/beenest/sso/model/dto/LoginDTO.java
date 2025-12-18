package club.beenest.sso.model.dto;

import lombok.Data;

/**
 * 登录请求 DTO
 */
@Data
public class LoginDTO {
    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;
}
