package club.beenest.sso.sdk.config;

import club.beenest.sso.api.service.ISsoService;
import club.beenest.sso.sdk.filter.SsoAuthFilter;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SsoSdkProperties.class)
public class SsoSdkAutoConfiguration {

    @DubboReference(check = false, protocol = "rest", url = "${sso.sdk.url:}")
    private ISsoService ssoService;

    @Bean
    @ConditionalOnProperty(prefix = "sso.sdk", name = "enable-filter", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<SsoAuthFilter> ssoAuthFilter(SsoSdkProperties properties) {
        FilterRegistrationBean<SsoAuthFilter> registration = new FilterRegistrationBean<>();
        // 确保 ssoService 已注入
        registration.setFilter(new SsoAuthFilter(ssoService, properties));
        registration.addUrlPatterns("/*");
        registration.setName("ssoAuthFilter");
        registration.setOrder(1); // 优先级较高
        return registration;
    }
}
