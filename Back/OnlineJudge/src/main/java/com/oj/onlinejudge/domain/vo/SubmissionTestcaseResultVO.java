package com.oj.onlinejudge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "提交-测试用例结果视图")
public class SubmissionTestcaseResultVO {
    private Long testcaseId;
    private Integer statusId;
    private String statusDescription;
    private String stdout;
    private String stderr;
    private String compileOutput;
    private String message;
    private BigDecimal timeUsed;
    private Integer memoryUsed;
}
