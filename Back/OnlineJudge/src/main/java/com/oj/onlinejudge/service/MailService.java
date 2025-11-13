package com.oj.onlinejudge.service;

public interface MailService {
    void sendVerificationCode(String to, String username, String code, long minutes);
    void sendRegisterSuccess(String to, String username, String role);
    void sendLoginNotice(String to, String username, String role, String ip);
    void sendLogoutNotice(String to, String username, String role);
}

