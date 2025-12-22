package club.beenest.sso.service;

import club.beenest.sso.model.dto.LoginDTO;
import club.beenest.sso.model.dto.LoginResultDTO;
import club.beenest.sso.model.dto.RegisterDTO;

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

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求信息
     */
    void register(RegisterDTO registerDTO);
}
