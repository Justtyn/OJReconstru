package com.oj.onlinejudge.domain.entity;

// 教师实体：映射表 teacher，含基本资料、主班级与登录信息

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("teacher")
@Schema(name = "Teacher", description = "教师实体")
public class Teacher {
    @TableId
    @Schema(description = "教师ID")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码哈希")
    private String password;
    @Schema(description = "姓名")
    private String name;
    @Schema(description = "性别")
    private String sex; // male/female/other/unknown
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "头像URL")
    private String avatar;
    @TableField("class_id")
    @Schema(description = "主班级ID")
    private Long classId;
    @Schema(description = "职称")
    private String title;
    @Schema(description = "最近登录时间")
    private LocalDateTime lastLoginTime;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
