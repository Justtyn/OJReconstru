package com.oj.onlinejudge.config;

// 配置静态资源映射：将 /files/avatars/** 映射到本地上传目录

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FileStorageConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    @PostConstruct
    public void init() {
        try {
            Path dir = Paths.get(storageProperties.getAvatarDir()).toAbsolutePath();
            java.nio.file.Files.createDirectories(dir);
            log.info("Avatar directory initialized at {}", dir);
        } catch (Exception ex) {
            log.warn("Failed to create avatar directory: {}", ex.getMessage());
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String prefix = storageProperties.getAvatarUrlPrefix();
        if (!StringUtils.hasText(prefix)) {
            return;
        }
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        Path avatarDir = Paths.get(storageProperties.getAvatarDir()).toAbsolutePath();
        String location = avatarDir.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler(prefix + "/**")
                .addResourceLocations(location);
    }
}
