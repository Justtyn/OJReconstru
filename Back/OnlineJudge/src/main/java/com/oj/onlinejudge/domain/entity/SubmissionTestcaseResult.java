package com.oj.onlinejudge.domain.entity;

// 提交单测试点结果实体：映射 submission_testcase_result 表

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
@TableName("submission_testcase_result")
@Schema(name = "SubmissionTestcaseResult", description = "提交-测试用例判题结果实体")
public class SubmissionTestcaseResult {
    @TableId
    @Schema(description = "记录ID")
    private Long id;
    @TableField("submission_id")
    @Schema(description = "提交ID")
    private Long submissionId;
    @TableField("testcase_id")
    @Schema(description = "测试用例ID")
    private Long testcaseId;
    @TableField("judge0_token")
    @Schema(description = "Judge0 Token")
    private String judge0Token;
    @TableField("status_id")
    @Schema(description = "Judge状态ID")
    private Integer statusId;
    @TableField("time_used")
    @Schema(description = "耗时(秒)")
    private BigDecimal timeUsed;
    @TableField("memory_used")
    @Schema(description = "内存(KB)")
    private Integer memoryUsed;
    @Schema(description = "标准输出")
    private String stdout;
    @Schema(description = "标准错误")
    private String stderr;
    @TableField("compile_output")
    @Schema(description = "编译输出")
    private String compileOutput;
    @Schema(description = "额外信息")
    private String message;
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
