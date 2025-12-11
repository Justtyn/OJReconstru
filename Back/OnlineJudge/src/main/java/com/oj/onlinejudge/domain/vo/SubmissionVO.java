package com.oj.onlinejudge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "提交摘要")
public class SubmissionVO {
    private Long id;
    private Long studentId;
    private Long problemId;
    private Long homeworkId;
    private Integer languageId;
    private Integer overallStatusId;
    private String overallStatusCode;
    private String overallStatusName;
    private Integer passedCaseCount;
    private Integer totalCaseCount;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
