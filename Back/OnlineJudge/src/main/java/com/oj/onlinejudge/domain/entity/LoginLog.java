package com.oj.onlinejudge.domain.entity;

// 登录日志实体：映射表 login_log，记录登录/登出、设备与IP等

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@TableName("login_log")
@Schema(description = "登录日志记录：保存用户登录/登出、IP、设备等信息")
public class LoginLog {
    @TableId
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "登录角色：admin/teacher/student")
    private String role; // admin/teacher/student
    @TableField("user_id")
    @Schema(description = "用户ID（对应具体角色表的主键）")
    private Long userId;
    @Schema(description = "用户名快照（历史追踪，不随改名回填）")
    private String username;
    @TableField("ip_address")
    @Schema(description = "登录IP地址")
    private String ipAddress;
    @Schema(description = "地理位置（IP解析结果，可为空）")
    private String location;
    @TableField("user_agent")
    @Schema(description = "User-Agent 客户端标识")
    private String userAgent;
    @Schema(description = "设备信息（如 iPhone、Windows10 等）")
    private String device;
    @TableField("login_time")
    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
    @TableField("logout_time")
    @Schema(description = "登出时间（未登出则为空）")
    private LocalDateTime logoutTime;
    @Schema(description = "是否登录成功，true 成功 false 失败")
    private Boolean success;
    @TableField("fail_reason")
    @Schema(description = "失败原因（成功时为空，例如密码错误、账号封禁等）")
    private String failReason;
    @Schema(description = "记录创建时间")
    private LocalDateTime createdAt;
    @Schema(description = "记录更新时间")
    private LocalDateTime updatedAt;
}
