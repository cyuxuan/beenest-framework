package club.beenest.sso.service;

import club.beenest.sso.model.dto.LoginDTO;
import club.beenest.sso.model.dto.LoginResultDTO;

/**
 * 认证服务接口
 */
public interface AuthService {
    /**
     * 用户登录
     *
     * @param loginDTO 登录请求信息
     * @return 登录结果 (包含 Token 等)
     */
    LoginResultDTO login(LoginDTO loginDTO);
}
