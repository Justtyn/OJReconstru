package com.oj.onlinejudge.security;

// 已认证用户对象：通过 JWT 解析得到，存放于请求属性 CURRENT_USER 中

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {

    public static final String REQUEST_ATTRIBUTE = "CURRENT_USER";

    private Long userId;

    private String username;

    // 新增：角色（student / teacher / admin）
    private String role;

    // 新增：原始 JWT Token（无前缀），用于 /me 返回
    private String token;
}
