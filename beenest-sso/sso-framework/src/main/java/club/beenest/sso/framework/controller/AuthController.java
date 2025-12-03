package club.beenest.sso.framwork.controller;

import cn.dev33.satoken.stp.StpUtil;
import club.beenest.sso.framwork.obj.LoginRequest;
import club.beenest.sso.framwork.obj.LoginResponse;
import club.beenest.sso.framwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    /**
     * 微服务登录接口
     * 微服务调用此接口进行用户认证
     */
    @PostMapping("/login")
    public ResponseEntity<?> microserviceLogin(@RequestBody LoginRequest loginRequest) {
        try {
            // 验证用户
            String id = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            if (id == null) {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setSuccess(false);
                loginResponse.setMessage("用户名或密码错误");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(loginResponse);
            }
            // 生成token
            StpUtil.login(id);
            // 获取token信息
            String token = StpUtil.getTokenValue();
            long expireTime = StpUtil.getTokenTimeout();
            // 构建响应
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(true);
            loginResponse.setMessage("登录成功");
            loginResponse.setToken(token);
            loginResponse.setUserId(id);
            loginResponse.setExpireTime(expireTime);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(false);
            loginResponse.setMessage("登录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(loginResponse);
        }
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            StpUtil.logout();
            return ResponseEntity.ok(Map.of("message", "登出成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "登出失败"));
        }
    }
}