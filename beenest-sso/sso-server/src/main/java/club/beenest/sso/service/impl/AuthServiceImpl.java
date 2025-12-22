package club.beenest.sso.service.impl;

import club.beenest.sso.common.enums.UserStatusEnum;
import club.beenest.sso.common.exception.ServiceException;
import club.beenest.sso.convert.AuthConverter;
import club.beenest.sso.mapper.SsoUserMapper;
import club.beenest.sso.model.dto.LoginDTO;
import club.beenest.sso.model.dto.LoginResultDTO;
import club.beenest.sso.model.dto.RegisterDTO;
import club.beenest.sso.model.entity.SsoUser;
import club.beenest.sso.service.AuthService;
import club.beenest.sso.util.AesUtil;
import club.beenest.sso.util.EncryptUtil;
import club.beenest.sso.util.JwtUtil;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${sa-token.token-name:sso-token}")
    private String tokenName;

    @Value("${sa-token.cookie.path:/}")
    private String cookiePath;

    @Value("${sa-token.cookie.domain:}")
    private String cookieDomain;

    @Value("${sa-token.cookie.max-age:2592000}")
    private int cookieMaxAge;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AesUtil aesUtil;

    @Autowired
    private EncryptUtil encryptUtil;

    @Autowired
    private SsoUserMapper ssoUserMapper;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求信息
     * @return 登录结果 (包含 Token 等)
     */
    @Override
    public LoginResultDTO login(LoginDTO loginDTO) {
        // 1. 从数据库中认证用户
        SsoUser user = ssoUserMapper.selectByAccount(loginDTO.getAccount());

        // 验证密码 (使用 EncryptUtil 加密后比对)
        if (Objects.nonNull(user) &&
                user.getPassword().equals(encryptUtil.encryptPassword(loginDTO.getPassword()))) {
            
            // 检查用户是否启用
            if (user.getStatus() == UserStatusEnum.DISABLE) {
                throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "账号已禁用");
            }

            // A. 生成原始 JWT
            String originalToken = jwtUtil.createToken(user.getAccount(), AuthConverter.ssoUserToMap(user));

            // B. 加密整个 JWT
            String encryptedToken = aesUtil.encrypt(originalToken);

            // C. Sa-Token 登录，并指定 Token 值
            StpUtil.login(user.getId(), SaLoginModel.create().setToken(encryptedToken));

            // D. 将 Token 写入 Cookie
            SaHolder.getResponse().addCookie(tokenName, encryptedToken, cookiePath, cookieDomain, cookieMaxAge);

            return new LoginResultDTO(encryptedToken);
        } else {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "凭证无效");
        }
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求信息
     */
    @Override
    public void register(RegisterDTO registerDTO) {
        // 1. 校验账号是否存在
        if (ssoUserMapper.selectCount(new LambdaQueryWrapper<SsoUser>()
                .eq(SsoUser::getAccount, registerDTO.getAccount())) > 0) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "账号已存在");
        }
        
        // 2. 校验用户名是否存在 (可选)
        if (ssoUserMapper.selectCount(new LambdaQueryWrapper<SsoUser>()
                .eq(SsoUser::getUsername, registerDTO.getUsername())) > 0) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "用户名已存在");
        }

        // 3. 构建用户对象
        SsoUser user = AuthConverter.registerDTOToSsoUser(registerDTO);

        // 4. 加密敏感信息
        user.setPassword(encryptUtil.encryptPassword(registerDTO.getPassword()));
        user.setEmail(encryptUtil.encryptContent(registerDTO.getEmail()));
        user.setPhone(encryptUtil.encryptContent(registerDTO.getPhone()));

        // 5. 保存
        ssoUserMapper.insert(user);
    }
}