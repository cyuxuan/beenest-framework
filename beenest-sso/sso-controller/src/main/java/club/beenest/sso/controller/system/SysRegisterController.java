package club.beenest.sso.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import club.beenest.sso.common.core.controller.BaseController;
import club.beenest.sso.common.core.domain.AjaxResult;
import club.beenest.sso.common.core.domain.model.RegisterBody;
import club.beenest.sso.common.utils.StringUtils;
import club.beenest.sso.framework.web.service.SysRegisterService;
import club.beenest.sso.system.service.ISysConfigService;

/**
 * 注册验证
 * 
 * @author beenest
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
