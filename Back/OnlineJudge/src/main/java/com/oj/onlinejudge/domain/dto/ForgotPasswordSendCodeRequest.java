package com.oj.onlinejudge.domain.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "找回密码-发送验证码请求")
public class ForgotPasswordSendCodeRequest {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;
}

