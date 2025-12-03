package club.beenest.sso.framwork.dao;

import club.beenest.sso.framwork.obj.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface UserMapper extends BaseMapper<User> {
    String getUserInfo(String account, String password);
}
