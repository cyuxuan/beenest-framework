package club.beenest.sso.convert;

import club.beenest.sso.common.enums.UserStatusEnum;
import club.beenest.sso.model.dto.RegisterDTO;
import club.beenest.sso.model.entity.SsoUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证相关对象转换器
 */
public class AuthConverter {

    /**
     * SsoUser 转 Map (用于 JWT Claims)
     *
     * @param ssoUser 用户实体
     * @return Map 对象
     */
    public static Map<String, Object> ssoUserToMap(SsoUser ssoUser) {
        if (ssoUser == null) {
            return null;
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("userId", ssoUser.getId());
        // 默认角色
        map.put("role", "user");
        map.put("account", ssoUser.getAccount());
        map.put("username", ssoUser.getUsername());
        
        return map;
    }

    /**
     * RegisterDTO 转 SsoUser
     *
     * @param registerDTO 注册信息
     * @return 用户实体
     */
    public static SsoUser registerDTOToSsoUser(RegisterDTO registerDTO) {
        if (registerDTO == null) {
            return null;
        }
        SsoUser user = new SsoUser();
        user.setAccount(registerDTO.getAccount());
        user.setUsername(registerDTO.getUsername());
        user.setAvatar(registerDTO.getAvatar());
        // 默认启用
        user.setStatus(UserStatusEnum.ENABLE);
        return user;
    }
}
