package club.beenest.sso.framwork.obj;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String userId;
    private String username;
    private Long expireTime;
}