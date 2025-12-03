package club.beenest.sso.framwork.obj;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class User {
    private String id;

    private String account;

    private String password;
}
