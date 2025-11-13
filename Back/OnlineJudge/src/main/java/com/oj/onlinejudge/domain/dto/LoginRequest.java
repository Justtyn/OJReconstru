package com.oj.onlinejudge.domain.dto;

// 登录请求 DTO：包含用户名、密码与可选角色

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "登录身份: student|teacher|admin, 默认为 student")
    private String role;
}
