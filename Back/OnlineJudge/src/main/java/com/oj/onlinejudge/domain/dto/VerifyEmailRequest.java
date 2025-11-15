package com.oj.onlinejudge.domain.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "邮箱验证码校验请求")
public class VerifyEmailRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String code;
}

