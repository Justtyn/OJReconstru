package com.oj.onlinejudge.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * HTTP 请求头名，用于读取 JWT。
     * 示例：Authorization: Bearer <token>
     */
    private String header = "Authorization";

    /**
     * Token 前缀（例如：Bearer）。
     * 解析时会根据该前缀剪裁真实 token。
     */
    private String prefix = "Bearer";

    /**
     * 签名密钥（务必妥善保管，勿明文提交仓库）。
     */
    private String secret;

    /**
     * Token 过期时间（单位：分钟）。
     */
    private long expireMinutes = 60;

    /**
     * 白名单路径（无需携带 JWT 即可访问）。
     * 支持 Ant 风格匹配，根据业务补充如：/api/auth/**、/v3/api-docs/** 等。
     */
    private List<String> whitelist = new ArrayList<>();

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpireMinutes() {
        return expireMinutes;
    }

    public void setExpireMinutes(long expireMinutes) {
        this.expireMinutes = expireMinutes;
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }
}
