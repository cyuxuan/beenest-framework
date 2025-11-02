package club.beenest.cs.controller.system;

import club.beenest.cs.core.service.ISysConfigService;
import club.beenest.cs.framework.web.service.SysRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import club.beenest.cs.common.core.controller.BaseController;
import club.beenest.cs.common.core.domain.AjaxResult;
import club.beenest.cs.common.core.domain.model.RegisterBody;
import club.beenest.cs.common.utils.StringUtils;

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
