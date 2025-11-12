package com.oj.onlinejudge.config;

// OpenAPI/Swagger 文档配置：启用文档与 Bearer 鉴权展示

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * 配置 OpenAPI 文档信息与 Bearer Token 安全方案
     */
    @Bean
    public OpenAPI openAPI(JwtProperties jwtProperties) {
        String schemeName = "BearerAuth";
        return new OpenAPI()
            .info(new Info()
                .title("Re Online Judge API")
                .version("1.0.0")
                .description("基础接口文档，涵盖判题平台常用能力"))
            .components(new Components().addSecuritySchemes(schemeName, new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name(jwtProperties.getHeader())))
            .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}
