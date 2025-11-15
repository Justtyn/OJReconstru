package com.oj.onlinejudge.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "找回密码-提交验证码并重置请求")
public class ForgotPasswordVerifyRequest {
    @NotBlank
    @Schema(description = "用户名")
    private String username;

    @NotBlank
    @Schema(description = "邮箱验证码")
    private String code;

    @NotBlank
    @Size(min = 6, max = 100, message = "密码长度需在6-100之间")
    @Schema(description = "新密码明文，将被哈希")
    private String newPassword;
}

