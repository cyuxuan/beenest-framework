package club.beenest.sso.service.impl;

import club.beenest.sso.common.exception.ServiceException;
import club.beenest.sso.convert.AuthConverter;
import club.beenest.sso.mapper.SsoUserMapper;
import club.beenest.sso.model.dto.LoginDTO;
import club.beenest.sso.model.dto.LoginResultDTO;
import club.beenest.sso.model.entity.SsoUser;
import club.beenest.sso.service.AuthService;
import club.beenest.sso.util.AesUtil;
import club.beenest.sso.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AesUtil aesUtil;

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

        if (user != null && user.getPassword().equals(loginDTO.getPassword())) {
            
            // 检查用户是否启用
            if (user.getStatus() != null && user.getStatus() == 0) {
                throw new ServiceException(401, "账号已禁用");
            }

            // B. 生成原始 JWT
            String originalToken = jwtUtil.createToken(user.getAccount(), AuthConverter.ssoUserToMap(user));

            // C. 加密整个 JWT
            String encryptedToken = aesUtil.encrypt(originalToken);

            return new LoginResultDTO(encryptedToken);
        } else {
            throw new ServiceException(401, "凭证无效");
        }
    }
}
