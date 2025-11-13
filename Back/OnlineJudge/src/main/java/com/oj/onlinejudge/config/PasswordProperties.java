package com.oj.onlinejudge.config;

// 密码相关配置：用于统一的盐值等安全参数

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.password")
public class PasswordProperties {

    /**
     * 全局盐值，在哈希用户密码前附加，提高安全性
     */
    private String salt = "change-this-salt";

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
