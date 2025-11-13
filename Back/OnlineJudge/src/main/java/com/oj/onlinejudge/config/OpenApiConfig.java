package com.oj.onlinejudge.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置：增加 Bearer 鉴权方案，方便在 Swagger UI 中携带 Token 调试。
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        final String schemeName = "BearerAuth";
        SecurityScheme bearer = new SecurityScheme()
                .name(schemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);
        return new OpenAPI()
                .info(new Info().title("Online Judge API").version("v1"))
                .components(new Components().addSecuritySchemes(schemeName, bearer))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}

