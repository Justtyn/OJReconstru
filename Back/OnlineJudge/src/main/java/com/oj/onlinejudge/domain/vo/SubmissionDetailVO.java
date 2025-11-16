package com.oj.onlinejudge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "提交详情")
public class SubmissionDetailVO extends SubmissionVO {
    private String sourceCode;
    private List<SubmissionTestcaseResultVO> testcaseResults;
}
