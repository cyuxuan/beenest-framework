package club.beenest.sso.framwork.obj;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String clientId; // 客户端ID
}