package com.oj.onlinejudge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域（CORS）配置。
 * 用途：
 * - 允许来自指定（或全部）来源的跨域请求；
 * - 统一暴露认证等必要响应头；
 * - 支持常见 HTTP 方法与自定义请求头；
 * - 通过预检请求（OPTIONS）缓存时长减少重复校验开销。
 * 提示：如需限制来源域名，建议将 allowedOriginPatterns("*") 调整为明确域名。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Authorization", "Content-Disposition")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
