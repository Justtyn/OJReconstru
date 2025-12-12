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
        String html = codeCard(
                "邮箱验证",
                "您的注册验证码如下：",
                username,
                code,
                minutes
        );
        sendHtml(to, "【Re-OnlineJudge】注册邮箱验证码", html);
    }

    @Override
    public void sendRegisterSuccess(String to, String username, String role) {
        String html = noticeCard(
                "注册成功",
                "欢迎加入 Re-OnlineJudge",
                username,
                role,
                "您的账号已激活，可以开始使用平台功能。祝您学习顺利。"
        );
        sendHtml(to, "【Re-OnlineJudge】注册成功", html);
    }

    @Override
    public void sendLoginNotice(String to, String username, String role, String ip) {
        String safeIp = (ip == null || ip.isBlank()) ? "未知" : ip;
        String html = noticeCard(
                "登录提醒",
                "检测到一次新的登录",
                username,
                role,
                "登录 IP：" + escape(safeIp)
        );
        sendHtml(to, "【Re-OnlineJudge】登录提醒", html);
    }

    @Override
    public void sendLogoutNotice(String to, String username, String role) {
        String html = noticeCard(
                "登出提醒",
                "您已从 Re-OnlineJudge 退出登录",
                username,
                role,
                "如果非本人操作，请尽快修改密码并联系管理员。"
        );
        sendHtml(to, "【Re-OnlineJudge】登出提醒", html);
    }

    @Override
    public void sendPasswordResetCode(String to, String username, String code, long minutes) {
        String html = codeCard(
                "找回密码",
                "您的找回密码验证码如下：",
                username,
                code,
                minutes
        );
        sendHtml(to, "【Re-OnlineJudge】找回密码验证码", html);
    }

    @Override
    public void sendPasswordChangedNotice(String to, String username, String role) {
        String html = noticeCard(
                "密码已更新",
                "您的账号密码已被修改",
                username,
                role,
                "如果非本人操作，请立即再次修改密码并联系管理员。"
        );
        sendHtml(to, "【Re-OnlineJudge】密码修改通知", html);
    }

    private String layout(String preheader, String innerHtml) {
        return ""
                + "<!doctype html>"
                + "<html lang='zh-CN'>"
                + "<head>"
                + "  <meta charset='UTF-8'/>"
                + "  <meta name='viewport' content='width=device-width,initial-scale=1'/>"
                + "  <meta name='x-apple-disable-message-reformatting'/>"
                + "  <title>Re-OnlineJudge</title>"
                + "  <style>"
                + "    body{margin:0;padding:0;background:#f5f7fb;} "
                + "    table{border-collapse:collapse;} "
                + "    img{border:0;line-height:100%;outline:none;text-decoration:none;} "
                + "    a{color:inherit;text-decoration:none;} "
                + "    .container{width:100%;background:#f5f7fb;padding:24px 12px;} "
                + "    .card{max-width:640px;margin:0 auto;background:#ffffff;border-radius:14px;overflow:hidden;"
                + "         box-shadow:0 8px 24px rgba(18,38,63,.08);} "
                + "    .header{background:linear-gradient(135deg,#2563eb,#60a5fa);padding:20px 24px;} "
                + "    .brand{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,'PingFang SC','Microsoft YaHei',sans-serif;"
                + "          color:#fff;font-weight:700;font-size:18px;letter-spacing:.3px;} "
                + "    .content{padding:24px;} "
                + "    .title{margin:0 0 8px;font-size:20px;color:#0f172a;font-weight:800;"
                + "          font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,'PingFang SC','Microsoft YaHei',sans-serif;} "
                + "    .subtitle{margin:0 0 16px;color:#475569;line-height:1.7;font-size:14px;"
                + "          font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,'PingFang SC','Microsoft YaHei',sans-serif;} "
                + "    .divider{height:1px;background:#e5e7eb;margin:18px 0;} "
                + "    .muted{color:#64748b;font-size:12px;line-height:1.6;"
                + "          font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,'PingFang SC','Microsoft YaHei',sans-serif;} "
                + "    .kv{margin:0;padding:0;list-style:none;} "
                + "    .kv li{margin:0 0 6px;color:#0f172a;font-size:13px;line-height:1.7;"
                + "          font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,'PingFang SC','Microsoft YaHei',sans-serif;} "
                + "    .label{color:#475569;font-weight:700;} "
                + "    .pill{display:inline-block;background:#eff6ff;color:#1d4ed8;padding:3px 10px;border-radius:999px;"
                + "          font-size:12px;font-weight:700;} "
                + "    .btn{display:inline-block;background:#2563eb;color:#ffffff;padding:10px 14px;border-radius:10px;"
                + "         font-weight:700;font-size:14px;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,'PingFang SC','Microsoft YaHei',sans-serif;} "
                + "    .codeWrap{margin:14px 0 10px;padding:14px 16px;background:#f8fafc;border:1px dashed #cbd5e1;border-radius:12px;"
                + "             text-align:center;} "
                + "    .code{font-size:30px;letter-spacing:6px;font-weight:900;color:#0f172a;"
                + "          font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,'Liberation Mono','Courier New',monospace;} "
                + "    .footer{padding:16px 24px;background:#f8fafc;color:#94a3b8;font-size:12px;line-height:1.6;"
                + "          font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,'PingFang SC','Microsoft YaHei',sans-serif;} "
                + "    @media (max-width:480px){"
                + "      .content{padding:18px;} "
                + "      .header{padding:18px;} "
                + "      .code{font-size:26px;letter-spacing:5px;} "
                + "    }"
                + "  </style>"
                + "</head>"
                + "<body>"
                + "  <div style='display:none;max-height:0;overflow:hidden;opacity:0;color:transparent;'>"
                + escape(preheader)
                + "  </div>"
                + "  <table class='container' role='presentation' width='100%'>"
                + "    <tr><td>"
                + "      <table class='card' role='presentation' width='100%'>"
                + "        <tr><td class='header'>"
                + "          <div class='brand'>Re-OnlineJudge</div>"
                + "        </td></tr>"
                + "        <tr><td class='content'>"
                + innerHtml
                + "        </td></tr>"
                + "        <tr><td class='footer'>"
                + "          本邮件由系统自动发送，请勿回复。<br/>"
                + "          如果这不是您本人操作，请尽快修改密码并联系管理员。"
                + "        </td></tr>"
                + "      </table>"
                + "    </td></tr>"
                + "  </table>"
                + "</body>"
                + "</html>";
    }

    private String infoList(String username, String role) {
        return ""
                + "<ul class='kv'>"
                + "  <li><span class='label'>用户名：</span>" + escape(username) + "</li>"
                + "  <li><span class='label'>角色：</span><span class='pill'>" + escape(role) + "</span></li>"
                + "</ul>";
    }

    private String noticeCard(String title, String subtitle, String username, String role, String message) {
        String inner = ""
                + "<h1 class='title'>" + escape(title) + "</h1>"
                + "<p class='subtitle'>" + escape(subtitle) + "</p>"
                + infoList(username, role)
                + "<div class='divider'></div>"
                + "<p class='subtitle' style='margin:0;color:#0f172a;'>" + escape(message) + "</p>"
                + "<div class='divider'></div>"
                + "<p class='muted'>如需帮助，请联系管理员或在站内查看安全设置。</p>";
        return layout(title + " - Re-OnlineJudge", inner);
    }

    private String codeCard(String title, String subtitle, String username, String code, long minutes) {
        String inner = ""
                + "<h1 class='title'>" + escape(title) + "</h1>"
                + "<p class='subtitle'>您好，" + escape(username) + "。</p>"
                + "<p class='subtitle' style='margin-top:0;'>" + escape(subtitle) + "</p>"
                + "<div class='codeWrap'>"
                + "  <div class='code'>" + escape(code) + "</div>"
                + "</div>"
                + "<p class='muted'>验证码有效期：<strong>" + minutes + "</strong> 分钟。请勿泄露给他人。</p>"
                + "<div class='divider'></div>"
                + "<p class='muted'>如果您未发起该请求，请忽略此邮件。</p>";
        return layout(title + " - 验证码", inner);
    }

    private String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
