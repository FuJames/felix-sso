package com.fqz.sso.example.config;

import com.fqz.sso.client.filter.SsoFilter;
import com.fqz.sso.client.model.Constant;
import com.fqz.sso.client.utils.JedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fuqianzhong
 * @date 19/1/8
 * config filter using @Configuration
 */
@Configuration
public class SsoConfiguration {
    @Value("${sso.server.address}")
    private String ssoServer;

    @Value("${sso.exclude.pattern}")
    private String excludePattern;

    @Value("${sso.redis.address}")
    private String redisAddress;

    // bean definition
    @Bean
    public FilterRegistrationBean ssoFilterBean(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setName("SsoFilter");
        registrationBean.setFilter(new SsoFilter());
        registrationBean.addInitParameter(Constant.SSO_SERVER,ssoServer);
        registrationBean.addInitParameter(Constant.SSO_EXCLUDE,excludePattern);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        JedisUtils.init(redisAddress);
        return registrationBean;
    }

    public String getSsoServer() {
        return ssoServer;
    }

    public void setSsoServer(String ssoServer) {
        this.ssoServer = ssoServer;
    }
}
