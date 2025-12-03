package club.beenest.sso.framwork.obj;

import lombok.Data;

@Data
public class TokenResponse {
    private String token;
    private String tokenName;
    private Long expire;
    private String realTokenName;
}
