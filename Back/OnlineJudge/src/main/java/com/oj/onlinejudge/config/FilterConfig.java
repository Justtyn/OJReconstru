package com.oj.onlinejudge.config;

// 注册 JWT 认证过滤器，拦截 /api/* 路径的请求并进行 Token 校验

import com.oj.onlinejudge.security.JwtAuthenticationFilter;
import com.oj.onlinejudge.security.JwtTokenProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    /**
     * 注册 JWT 过滤器，设置匹配路径与优先级
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
        JwtTokenProvider jwtTokenProvider,
        JwtProperties jwtProperties) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthenticationFilter(jwtTokenProvider, jwtProperties));
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}
