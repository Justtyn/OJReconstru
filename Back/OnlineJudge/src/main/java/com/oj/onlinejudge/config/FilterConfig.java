package com.oj.onlinejudge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.security.JwtAuthenticationFilter;
import com.oj.onlinejudge.security.JwtTokenProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 认证过滤器注册。
 * 用途：针对 /api/* 路径进行 Token 校验与放行控制。
 * 说明：
 * - 此处仅负责 Filter 的装配与路径匹配；
 * - 具体认证逻辑由 JwtAuthenticationFilter 完成；
 * - 可按需调整顺序与匹配路径以适配接口规划。
 */
@Configuration
public class FilterConfig {

    /**
     * 注册 JWT 过滤器。
     * 参数：
     * - jwtTokenProvider：JWT 的生成与解析工具
     * - jwtProperties：JWT 相关配置（请求头、前缀、白名单等）
     * 行为：拦截 /api/* 请求，按 order 顺序优先执行。
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtTokenProvider jwtTokenProvider,
            JwtProperties jwtProperties,
            ObjectMapper objectMapper) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthenticationFilter(jwtTokenProvider, jwtProperties, objectMapper));
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}
