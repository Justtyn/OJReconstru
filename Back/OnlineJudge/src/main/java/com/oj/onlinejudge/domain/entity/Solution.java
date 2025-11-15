package com.oj.onlinejudge.domain.entity;

// 题解实体：映射 solution 表

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("solution")
@Schema(name = "Solution", description = "题解实体")
public class Solution {
    @TableId
    @Schema(description = "题解ID")
    private Long id;
    @TableField("user_id")
    @Schema(description = "发布者学生ID")
    private Long userId;
    @TableField("problem_id")
    @Schema(description = "题目ID")
    private Long problemId;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容")
    private String content;
    @Schema(description = "语言")
    private String language;
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    @TableField("is_active")
    @Schema(description = "是否启用")
    private Boolean isActive;
}
