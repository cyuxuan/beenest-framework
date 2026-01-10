package club.beenest.sso.sdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sso.sdk")
public class SsoSdkProperties {
    /**
     * SSO 服务地址 (直接指定 URL，不走注册中心)
     * 例如: http://localhost:8080
     */
    private String url;

    /**
     * SSO 服务名称 (用于 FeignClient)
     */
    private String serviceName = "beenest-sso";

    /**
     * Token 名称 (Cookie 名称)
     */
    private String tokenName = "sso-token";

    /**
     * 是否启用过滤器
     */
    private boolean enableFilter = true;

    /**
     * 排除的路径 (逗号分隔)
     */
    private String excludePaths = "login";
}
