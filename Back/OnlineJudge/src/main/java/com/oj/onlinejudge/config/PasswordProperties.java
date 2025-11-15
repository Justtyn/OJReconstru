package com.oj.onlinejudge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 密码相关配置。
 * 用途：集中管理与密码哈希相关的安全参数（如全局盐值）。
 * 注意：生产环境建议从安全的配置中心或环境变量加载敏感信息，不要硬编码。
 */
@ConfigurationProperties(prefix = "security.password")
public class PasswordProperties {

    /**
     * 全局盐值：在对用户密码做哈希前附加此盐值，提高对简单彩虹表的抵抗能力。
     */
    private String salt = "change-this-salt";

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
