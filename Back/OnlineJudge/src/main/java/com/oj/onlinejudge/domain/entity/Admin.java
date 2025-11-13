package com.oj.onlinejudge.domain.entity;

// 管理员实体：映射表 admin，包含基础账号与审计字段

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("admin")
@Schema(name = "Admin", description = "管理员实体")
public class Admin {
    @TableId
    @Schema(description = "管理员ID")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码哈希")
    private String password;
    @Schema(description = "姓名")
    private String name;
    @Schema(description = "性别")
    private String sex; // male/female/other/unknown
    @Schema(description = "出生日期")
    private LocalDate birth;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "头像URL")
    private String avatar;
    @Schema(description = "最近登录IP")
    private String lastLoginIp;
    @Schema(description = "最近登录时间")
    private LocalDateTime lastLoginTime;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
