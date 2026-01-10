package club.beenest.sso.controller;

import club.beenest.sso.api.model.SsoResult;
import club.beenest.sso.common.response.Response;
import club.beenest.sso.model.dto.LoginDTO;
import club.beenest.sso.model.dto.LoginResultDTO;
import club.beenest.sso.model.dto.RegisterDTO;
import club.beenest.sso.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 提供登录和注册接口 (面向前端)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    /**
     * 登录接口
     *
     * @param loginDTO 登录参数 (账号、密码)
     * @return 登录结果
     */
    @PostMapping("/login")
    public Response<LoginResultDTO> login(@RequestBody LoginDTO loginDTO) {
        LoginResultDTO result = authService.login(loginDTO);
        return Response.success("登录成功", result);
    }

    /**
     * 注册接口
     *
     * @param registerDTO 注册参数
     * @return 注册结果
     */
    @PostMapping("/register")
    public Response<Void> register(@RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Response.success("注册成功", null);
    }

    /**
     * 验证 Token (提供给 SDK 调用)
     *
     * @param token 令牌
     * @return 验证结果
     */
    @PostMapping("/verify")
    public SsoResult<Boolean> verify(@RequestParam("token") String token) {
        boolean isValid = authService.verify(token);
        return SsoResult.success(isValid);
    }
}
