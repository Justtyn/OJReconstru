package com.oj.onlinejudge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "可视化-全局概览")
public class AnalyticsSummaryVO {

    @Schema(description = "提交总数")
    private Long submissionTotal;

    @Schema(description = "AC 提交数")
    private Long submissionAccepted;

    @Schema(description = "启用题目数")
    private Long problemActive;

    @Schema(description = "禁用题目数")
    private Long problemInactive;

    @Schema(description = "题解总数")
    private Long solutionTotal;

    @Schema(description = "启用题解数")
    private Long solutionActive;

    @Schema(description = "讨论总数")
    private Long discussionTotal;

    @Schema(description = "启用讨论数")
    private Long discussionActive;

    @Schema(description = "登录成功次数")
    private Long loginSuccess;

    @Schema(description = "登录失败次数")
    private Long loginFail;
}
