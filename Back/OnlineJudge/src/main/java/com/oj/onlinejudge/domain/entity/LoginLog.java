package com.oj.onlinejudge.domain.entity;

// 登录日志实体：映射表 login_log，记录登录/登出、设备与IP等

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("login_log")
@Schema(name = "LoginLog", description = "登录日志实体")
public class LoginLog {
    @TableId
    @Schema(description = "日志ID")
    private Long id;
    @Schema(description = "角色：admin/teacher/student")
    private String role; // admin/teacher/student
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户名")
    private String username;
    @TableField("ip_address")
    @Schema(description = "IP地址")
    private String ipAddress;
    @Schema(description = "地理位置")
    private String location;
    @TableField("user_agent")
    @Schema(description = "User-Agent")
    private String userAgent;
    @Schema(description = "设备信息")
    private String device;
    @TableField("login_time")
    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
    @TableField("logout_time")
    @Schema(description = "登出时间")
    private LocalDateTime logoutTime;
    @Schema(description = "是否成功")
    private Boolean success;
    @TableField("fail_reason")
    @Schema(description = "失败原因")
    private String failReason;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
