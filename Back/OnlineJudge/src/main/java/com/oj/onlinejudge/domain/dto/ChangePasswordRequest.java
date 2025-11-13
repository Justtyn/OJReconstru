package com.oj.onlinejudge.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改密码请求")
public class ChangePasswordRequest {
    @NotBlank
    @Schema(description = "旧密码")
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 100, message = "密码长度需在6-100之间")
    @Schema(description = "新密码")
    private String newPassword;
}

