package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "登录日志创建/更新请求")
public class LoginLogRequest {

    @NotBlank(groups = CreateGroup.class, message = "角色不能为空")
    private String role;

    @NotNull(groups = CreateGroup.class, message = "用户ID不能为空")
    private Long userId;

    @NotBlank(groups = CreateGroup.class, message = "用户名不能为空")
    private String username;

    private String ipAddress;

    private String location;

    private String userAgent;

    private String device;

    private LocalDateTime loginTime;

    private LocalDateTime logoutTime;

    private Boolean success;

    private String failReason;
}
