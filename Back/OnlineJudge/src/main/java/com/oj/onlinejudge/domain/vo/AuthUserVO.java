package com.oj.onlinejudge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AuthUserVO", description = "认证用户信息及令牌")
public class AuthUserVO {
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "头像URL")
    private String avatar;
    @Schema(description = "角色 student|teacher|admin")
    private String role;
    @Schema(description = "带前缀的JWT令牌")
    private String token;
    @Schema(description = "完整用户属性快照，包含该角色实体所有字段")
    private Map<String, Object> details; // 新增：返回全部属性
}
