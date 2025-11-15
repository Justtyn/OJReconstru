package com.oj.onlinejudge.config;

// 本地文件存储配置：头像上传目录与对外暴露前缀

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    /**
     * 头像文件在服务器上的根目录，支持相对路径。
     */
    private String avatarDir = "./uploads/avatars";

    /**
     * 对外访问头像资源的 URL 前缀，例如 /files/avatars。
     */
    private String avatarUrlPrefix = "/files/avatars";
}
