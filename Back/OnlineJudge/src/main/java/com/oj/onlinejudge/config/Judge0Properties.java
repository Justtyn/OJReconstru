package com.oj.onlinejudge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 是否让 Judge0 请求阻塞等待最终结果（wait=true）。
     *
     * <p>默认 false：仅返回 token 和初始状态，后续通过轮询查询结果。
     */
    private boolean wait = false;

    /**
     * Judge0 语言 ID -> 名称映射。
     */
    private Map<Integer, String> languages = new HashMap<>();

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

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public Map<Integer, String> getLanguages() {
        return languages;
    }

    public void setLanguages(Map<Integer, String> languages) {
        this.languages = languages;
    }
}
