package com.oj.onlinejudge.domain.entity;

// 学生实体：映射表 student，包含做题统计、学校、登录及个性化字段

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@TableName("student")
@Schema(description = "学生：平台学员账户与学习统计")
public class Student {
    @TableId
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "用户名（唯一）")
    private String username;
    @Schema(description = "密码（哈希存储）")
    private String password;
    @Schema(description = "姓名")
    private String name;
    @Schema(description = "性别：male/female/other/unknown")
    private String sex;
    @Schema(description = "出生日期")
    private LocalDate birth;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "头像URL")
    private String avatar;
    @Schema(description = "个人主页背景图URL")
    private String background;
    @Schema(description = "通过数（AC）")
    private Integer ac;
    @Schema(description = "提交数")
    private Integer submit;
    @Schema(description = "学校")
    private String school;
    @Schema(description = "总得分")
    private Integer score;
    @TableField("last_login_time")
    @Schema(description = "最近登录时间")
    private LocalDateTime lastLoginTime;
    @TableField("last_language")
    @Schema(description = "上次提交的编程语言")
    private String lastLanguage;
    @TableField("create_time")
    @Schema(description = "账号创建时间")
    private LocalDateTime createTime;
    @TableField("last_visit_time")
    @Schema(description = "最近访问时间")
    private LocalDateTime lastVisitTime;
    @TableField("class_id")
    @Schema(description = "所属班级ID")
    private Long classId;
    @TableField("daily_challenge")
    @Schema(description = "日常挑战进度/状态")
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
    @Schema(description = "是否已验证（邮箱等）")
    private Boolean isVerified;
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
