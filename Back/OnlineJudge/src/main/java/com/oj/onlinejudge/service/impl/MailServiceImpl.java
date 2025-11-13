package com.oj.onlinejudge.service.impl;

import com.oj.onlinejudge.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    private void sendHtml(String to, String subject, String html) {
        if (!mailEnabled) return; // skip in tests or when disabled
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            // log and ignore to not break the business flow
            System.err.println("Mail send failed: " + e.getMessage());
        }
    }

    @Override
    public void sendVerificationCode(String to, String username, String code, long minutes) {
        String html = "<div style='font-family:Arial,Helvetica,sans-serif;max-width:600px;margin:auto;padding:24px;border:1px solid #eee;border-radius:8px;'>" +
                "<h2 style='color:#2b6cb0;margin:0 0 12px;'>Re-OnlineJudge 邮箱验证</h2>" +
                "<p>您好，" + escape(username) + "：</p>" +
                "<p>您的注册验证码为：</p>" +
                "<div style='font-size:28px;font-weight:700;letter-spacing:4px;color:#1a202c;padding:12px 16px;background:#f7fafc;border-radius:6px;display:inline-block;'>" + code + "</div>" +
                "<p style='color:#718096;margin-top:12px;'>验证码将在 " + minutes + " 分钟后过期，请尽快完成验证。</p>" +
                "<hr style='border:none;border-top:1px solid #e2e8f0;margin:20px 0;'>" +
                "<p style='font-size:12px;color:#a0aec0;'>如果您未发起注册请求，请忽略此邮件。</p>" +
                "</div>";
        sendHtml(to, "【Re-OnlineJudge】注册邮箱验证码", html);
    }

    @Override
    public void sendRegisterSuccess(String to, String username, String role) {
        String html = baseCard("注册成功通知", "欢迎加入 Re-OnlineJudge！", username, role,
                "您的账号已激活，可以开始使用平台功能。祝您学习顺利！");
        sendHtml(to, "【Re-OnlineJudge】注册成功", html);
    }

    @Override
    public void sendLoginNotice(String to, String username, String role, String ip) {
        String html = baseCard("登录提醒", "检测到一次新的登录", username, role,
                "IP 地址：" + escape(ip == null ? "未知" : ip));
        sendHtml(to, "【Re-OnlineJudge】登录提醒", html);
    }

    @Override
    public void sendLogoutNotice(String to, String username, String role) {
        String html = baseCard("登出提醒", "您已从 Re-OnlineJudge 退出登录", username, role,
                "如果非本人操作，请尽快修改密码并联系管理员。");
        sendHtml(to, "【Re-OnlineJudge】登出提醒", html);
    }

    @Override
    public void sendPasswordResetCode(String to, String username, String code, long minutes) {
        String html = "<div style='font-family:Arial,Helvetica,sans-serif;max-width:600px;margin:auto;padding:24px;border:1px solid #eee;border-radius:8px;'>" +
                "<h2 style='color:#2b6cb0;margin:0 0 12px;'>Re-OnlineJudge 找回密码</h2>" +
                "<p>您好，" + escape(username) + "：</p>" +
                "<p>您的找回密码验证码为：</p>" +
                "<div style='font-size:28px;font-weight:700;letter-spacing:4px;color:#1a202c;padding:12px 16px;background:#f7fafc;border-radius:6px;display:inline-block;'>" + code + "</div>" +
                "<p style='color:#718096;margin-top:12px;'>验证码将在 " + minutes + " 分钟后过期，请尽快完成验证。</p>" +
                "<hr style='border:none;border-top:1px solid #e2e8f0;margin:20px 0;'>" +
                "<p style='font-size:12px;color:#a0aec0;'>如果您未发起操作，请忽略此邮件。</p>" +
                "</div>";
        sendHtml(to, "【Re-OnlineJudge】找回密码验证码", html);
    }

    @Override
    public void sendPasswordChangedNotice(String to, String username, String role) {
        String html = baseCard("密码已更新", "您的账号密码已被修改", username, role,
                "如果非本人操作，请立即再次修改密码并联系管理员。");
        sendHtml(to, "【Re-OnlineJudge】密码修改通知", html);
    }

    private String baseCard(String title, String subtitle, String username, String role, String message) {
        return "<div style='font-family:Arial,Helvetica,sans-serif;max-width:600px;margin:auto;padding:24px;border:1px solid #eee;border-radius:8px;'>" +
                "<h2 style='color:#2b6cb0;margin:0 0 8px;'>" + escape(title) + "</h2>" +
                "<p style='margin:0 0 12px;color:#4a5568;'>" + escape(subtitle) + "</p>" +
                "<ul style='padding-left:16px;color:#2d3748;line-height:1.8;'>" +
                "<li><strong>用户名：</strong>" + escape(username) + "</li>" +
                "<li><strong>角色：</strong>" + escape(role) + "</li>" +
                "</ul>" +
                "<p style='color:#2d3748;'>" + escape(message) + "</p>" +
                "<hr style='border:none;border-top:1px solid #e2e8f0;margin:20px 0;'>" +
                "<p style='font-size:12px;color:#a0aec0;'>本邮件由系统自动发送，请勿回复。</p>" +
                "</div>";
    }

    private String escape(String s) { return s == null ? "" : s.replace("<","&lt;").replace(">","&gt;"); }
}
