package club.beenest.sso.sdk.filter;

import club.beenest.sso.api.model.SsoResult;
import club.beenest.sso.api.service.ISsoService;
import club.beenest.sso.sdk.config.SsoSdkProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SSO 认证过滤器
 * 基于 Dubbo 调用 SSO 服务验证 Token
 */
@Slf4j
public class SsoAuthFilter implements Filter {

    private final ISsoService ssoService;
    private final SsoSdkProperties ssoSdkProperties;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    public SsoAuthFilter(ISsoService ssoService, SsoSdkProperties ssoSdkProperties) {
        this.ssoService = ssoService;
        this.ssoSdkProperties = ssoSdkProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 1. 检查排除路径
        if (isExcluded(req.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 获取 Token
        String token = this.getTokenFromCookie(req);

        // 3. 如果没有 Token，返回 403
        if (token == null || token.isEmpty()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "SSO Auth: No Token Found");
            return;
        }

        // 4. 验证 Token (通过 Dubbo 调用)
        try {
            SsoResult<Boolean> result = ssoService.verify(token);
            if (result != null && result.isSuccess() && Boolean.TRUE.equals(result.getData())) {
                // 验证通过
                chain.doFilter(request, response);
            } else {
                // 验证失败
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "SSO Auth: Invalid Token");
            }
        } catch (Exception e) {
            log.error("SSO Auth: Verify Error", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SSO Auth: Service Error");
        }
    }

    private boolean isExcluded(String uri) {
        String excludePaths = ssoSdkProperties.getExcludePaths();
        if (excludePaths == null || excludePaths.isEmpty()) {
            return false;
        }
        for (String pattern : excludePaths.split(",")) {
            if (pathMatcher.match(pattern.trim(), uri)) {
                return true;
            }
        }
        return false;
    }

    private String getTokenFromCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ssoSdkProperties.getTokenName().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void destroy() {
    }
}
