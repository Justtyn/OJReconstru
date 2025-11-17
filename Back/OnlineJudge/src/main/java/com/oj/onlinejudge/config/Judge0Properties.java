package com.oj.onlinejudge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "judge0")
public class Judge0Properties {

    /**
     * Judge0 服务基础地址，例如：http://139.59.227.54:2358
     */
    private String baseUrl;

    /**
     * HTTP 请求超时时间（毫秒）
     */
    private int timeoutMs = 15000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}
