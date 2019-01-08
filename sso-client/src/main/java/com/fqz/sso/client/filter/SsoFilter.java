package com.fqz.sso.client.filter;

import com.fqz.sso.client.model.Constant;
import com.fqz.sso.client.model.SsoUser;
import com.fqz.sso.client.utils.CookieUtils;
import com.fqz.sso.client.utils.JedisUtils;
import com.fqz.sso.client.utils.TokenUtils;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author fuqianzhong
 * @date 19/1/2
 */
public class SsoFilter implements Filter {
    private static final AntPathMatcher antpathMatcher = new AntPathMatcher();
    private String ssoServer;
    private String excludePathPatterns;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //get ssoServer address from filter defined in webApp
        ssoServer = filterConfig.getInitParameter(Constant.SSO_SERVER);
        //antpath matcher , splict by comma
        excludePathPatterns = filterConfig.getInitParameter(Constant.SSO_EXCLUDE);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if(excludePathPatterns != null){
            String excludePathPatternArr[] = excludePathPatterns.split(",");
            String currentPath = req.getRequestURI().toString();
            for(String exludePathPattern : excludePathPatternArr){
                if(antpathMatcher.match(exludePathPattern,currentPath)){
                    chain.doFilter(request,response);
                    return;
                }
            }
        }

        // get token from request
        String token = request.getParameter(Constant.SSO_TOKEN);
        if(token == null || token.trim().length() <= 0){
            // get token from cookie
            Cookie cookie = CookieUtils.getCookie(Constant.SSO_TOKEN, req);
            if(cookie != null) {
                token = cookie.getValue();
            }
        }

        SsoUser ssoUser = null;
        //get ssoUser from token
        if(token != null && token.trim().length() > 0){
            String storeKey = TokenUtils.parseToStoreKey(token);
            ssoUser = (SsoUser) JedisUtils.getObject(storeKey);
            if(ssoUser != null){
                // valid user , continue
                request.setAttribute(Constant.SSO_USER, ssoUser);
                chain.doFilter(request,response);
            }
        }

        //invalid user, redirect to login page
        if(ssoUser == null) {
            String redirectUrl = req.getParameter(Constant.REDIRECT_URL);
            if(redirectUrl == null){
                redirectUrl = req.getRequestURL().toString();
            }
            res.sendRedirect(ssoServer + "/login" + "?" + Constant.REDIRECT_URL + "=" + redirectUrl);
        }

    }

    @Override
    public void destroy() {

    }
}
