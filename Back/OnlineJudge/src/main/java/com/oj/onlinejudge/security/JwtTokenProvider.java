package com.oj.onlinejudge.security;

// JWT Token 工具：负责生成、解析、验证 Token
// - generateToken: 生成带用户名、ID 与 角色 的 JWT
// - parseUser: 解析为认证用户对象
// - resolveToken: 从请求头去掉前缀提取实际 Token

import com.oj.onlinejudge.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /** 初始化 HMAC 密钥 */
    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /** 生成用户 Token（保持旧签名兼容，默认 student） */
    public String generateToken(Long userId, String username) {
        return generateToken(userId, username, "student");
    }

    /** 生成包含角色的用户 Token */
    public String generateToken(Long userId, String username, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.getExpireMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("username", username)
            .claim("role", role)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    /** 从 Token 中解析认证用户 */
    public AuthenticatedUser parseUser(String token) {
        Claims claims = parseClaims(token);
        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);
        if (role == null) role = "student"; // 兼容旧 token
        // 将原始 token 设置进去
        return new AuthenticatedUser(userId, username, role, token);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /** 去除前缀，提取原始 Token 字符串 */
    public String resolveToken(String headerValue) {
        if (!StringUtils.hasText(headerValue)) {
            return null;
        }
        String prefix = jwtProperties.getPrefix() + " ";
        if (headerValue.startsWith(prefix)) {
            return headerValue.substring(prefix.length());
        }
        return headerValue;
    }
}
