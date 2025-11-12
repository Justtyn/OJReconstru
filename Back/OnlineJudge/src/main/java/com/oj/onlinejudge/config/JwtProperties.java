package com.oj.onlinejudge.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * HTTP 请求头名称，JWT 从该头部读取
     */
    private String header = "Authorization";

    /**
     * Token 前缀（如 Bearer），返回给前端或解析时使用
     */
    private String prefix = "Bearer";

    /**
     * 签名与验证所用的密钥
     */
    private String secret;

    /**
     * Token 过期时间（分钟）
     */
    private long expireMinutes = 60;

    /**
     * 白名单接口列表，这些路径不进行 JWT 校验
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
