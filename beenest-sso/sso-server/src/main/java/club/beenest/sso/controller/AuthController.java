package club.beenest.sso.controller;

import club.beenest.sso.common.response.Response;
import club.beenest.sso.model.dto.LoginDTO;
import club.beenest.sso.model.dto.LoginResultDTO;
import club.beenest.sso.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 提供登录和 Token 验证接口
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
}
