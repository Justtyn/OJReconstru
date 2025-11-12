package com.oj.onlinejudge.security;

// 密码服务：统一进行密码加密与校验，内置全局盐值以增强安全性

import com.oj.onlinejudge.config.PasswordProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordService {

    private final PasswordProperties passwordProperties;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PasswordService(PasswordProperties passwordProperties) {
        this.passwordProperties = passwordProperties;
    }

    /** 将明文密码加盐后进行BCrypt哈希 */
    public String encode(String plainPassword) {
        return passwordEncoder.encode(applySalt(plainPassword));
    }

    /** 校验明文密码与哈希是否匹配 */
    public boolean matches(String plainPassword, String encodedPassword) {
        return passwordEncoder.matches(applySalt(plainPassword), encodedPassword);
    }

    /** 附加全局盐值 */
    private String applySalt(String plainPassword) {
        return passwordProperties.getSalt() + plainPassword;
    }
}
