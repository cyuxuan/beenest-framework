package club.beenest.sso.framwork.service;

import club.beenest.sso.framwork.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userRepository;

    /**
     * 用户登录验证
     */
    public String login(String username, String password) {
        return userRepository.getUserInfo(username,password);
    }
}