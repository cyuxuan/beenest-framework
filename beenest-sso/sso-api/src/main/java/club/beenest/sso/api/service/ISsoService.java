package club.beenest.sso.api.service;

import club.beenest.sso.api.model.SsoResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * SSO 服务 Dubbo 接口
 */
@RequestMapping("/api/auth")
public interface ISsoService {

    /**
     * 验证 Token 是否有效
     *
     * @param token 令牌
     * @return 验证结果 (true: 有效, false: 无效)
     */
    @PostMapping("/verify")
    SsoResult<Boolean> verify(@RequestParam("token") String token);
}
