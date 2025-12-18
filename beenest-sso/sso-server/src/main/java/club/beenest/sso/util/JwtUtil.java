package club.beenest.sso.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {

    /**
     * 密钥
     */
    @Value("${sso.jwt.secret:beenest_sso_secret_key_beenest_sso_secret_key}")
    private String secret;

    /**
     * 过期时间 (毫秒)
     */
    @Value("${sso.jwt.expiration:86400000}") // 1 day
    private long expiration;

    /**
     * 创建 Token
     *
     * @param subject 主题 (通常是用户名或ID)
     * @param claims  自定义声明
     * @return JWT Token 字符串
     */
    public String createToken(String subject, Map<String, Object> claims) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, signingKey);

        if (expiration > 0) {
            long expMillis = nowMillis + expiration;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    /**
     * 解析 Token
     *
     * @param token JWT Token 字符串
     * @return Claims 声明
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .parseClaimsJws(token)
                .getBody();
    }
}
