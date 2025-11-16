package com.oj.onlinejudge.domain.entity;

// 学生实体：映射表 student，包含做题统计、学校、登录及个性化字段

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
@TableName("student")
@Schema(name = "Student", description = "学生实体")
public class Student {
    @TableId
    @Schema(description = "学生ID")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码哈希")
    private String password;
    @Schema(description = "姓名")
    private String name;
    @Schema(description = "性别")
    private String sex;
    @Schema(description = "生日")
    private LocalDate birth;
    @Schema(description = "电话")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "头像URL")
    private String avatar;
    @Schema(description = "背景图URL")
    private String background;
    @Schema(description = "AC题数")
    private Integer ac;
    @Schema(description = "提交次数")
    private Integer submit;
    @Schema(description = "学校名称")
    private String school;
    @Schema(description = "积分/分数")
    private Integer score;
    @TableField("last_login_time")
    @Schema(description = "最近登录时间")
    private LocalDateTime lastLoginTime;
    @TableField("last_language")
    @Schema(description = "最近使用语言")
    private String lastLanguage;
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @TableField("last_visit_time")
    @Schema(description = "最近访问时间")
    private LocalDateTime lastVisitTime;
    @TableField("daily_challenge")
    @Schema(description = "今日挑战标识")
    private String dailyChallenge;
    @TableField("register_ip")
    @Schema(description = "注册IP")
    private String registerIp;
    @TableField("last_login_ip")
    @Schema(description = "最近登录IP")
    private String lastLoginIp;
    @Schema(description = "个人简介")
    private String bio;
    @TableField("is_verified")
    @Schema(description = "是否已验证")
    private Boolean isVerified;
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
