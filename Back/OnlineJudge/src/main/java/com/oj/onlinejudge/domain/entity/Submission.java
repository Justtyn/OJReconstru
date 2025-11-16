package com.oj.onlinejudge.domain.entity;

// 提交实体：映射 submission 表

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.Data;

@Data
@TableName("submission")
@Schema(name = "Submission", description = "提交实体")
public class Submission {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "提交ID")
    private Long id;
    @TableField("student_id")
    @Schema(description = "学生ID")
    private Long studentId;
    @TableField("problem_id")
    @Schema(description = "题目ID")
    private Long problemId;
    @TableField("homework_id")
    @Schema(description = "所属作业ID")
    private Long homeworkId;
    @TableField("language_id")
    @Schema(description = "Judge0语言ID")
    private Integer languageId;
    @TableField("source_code")
    @Schema(description = "源代码")
    private String sourceCode;
    @TableField("overall_status_id")
    @Schema(description = "整体状态ID")
    private Integer overallStatusId;
    @TableField("passed_case_count")
    @Schema(description = "通过用例数")
    private Integer passedCaseCount;
    @TableField("total_case_count")
    @Schema(description = "总用例数")
    private Integer totalCaseCount;
    @Schema(description = "得分")
    private Integer score;
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
