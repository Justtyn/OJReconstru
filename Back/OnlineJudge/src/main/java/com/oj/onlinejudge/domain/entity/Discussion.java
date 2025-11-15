package com.oj.onlinejudge.domain.entity;

// 讨论实体：映射 discussion 表

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.Data;

@Data
@TableName("discussion")
@Schema(name = "Discussion", description = "讨论实体")
public class Discussion {
    @TableId
    @Schema(description = "讨论ID")
    private Long id;
    @TableField("user_id")
    @Schema(description = "发布者学生ID")
    private Long userId;
    @TableField("problem_id")
    @Schema(description = "关联题目ID")
    private Long problemId;
    @Schema(description = "标题")
    private String title;
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "讨论内容")
    private String content;
    @TableField("view_num")
    @Schema(description = "浏览量")
    private Integer viewNum;
    @TableField("is_active")
    @Schema(description = "是否启用")
    private Boolean isActive;
}
