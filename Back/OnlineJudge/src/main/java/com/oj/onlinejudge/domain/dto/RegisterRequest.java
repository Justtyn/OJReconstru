package com.oj.onlinejudge.domain.dto;

// 注册请求 DTO：用户名、密码、邮箱、姓名

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户注册请求")
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度需在6-100之间")
    @Schema(description = "密码明文，将被哈希")
    private String password;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "姓名")
    private String name;
}

