package com.oj.onlinejudge.domain.vo;

// 用户视图对象：登录或查询返回的用户基础信息与 Token

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "用户返回视图对象")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "认证 Token (Bearer ...) ")
    private String token;
}

