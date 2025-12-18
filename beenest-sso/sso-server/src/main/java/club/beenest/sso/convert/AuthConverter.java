package club.beenest.sso.convert;

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
        map.put("role", "user"); // 默认角色
        map.put("account", ssoUser.getAccount());
        map.put("username", ssoUser.getUsername());
        
        return map;
    }
}
