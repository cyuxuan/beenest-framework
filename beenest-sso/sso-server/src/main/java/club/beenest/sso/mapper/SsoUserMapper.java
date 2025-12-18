package club.beenest.sso.mapper;

import club.beenest.sso.model.entity.SsoUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SsoUserMapper extends BaseMapper<SsoUser> {
    /**
     * 根据账号查询用户
     *
     * @param account 账号
     * @return 用户信息
     */
    SsoUser selectByAccount(@Param("account") String account);
}
