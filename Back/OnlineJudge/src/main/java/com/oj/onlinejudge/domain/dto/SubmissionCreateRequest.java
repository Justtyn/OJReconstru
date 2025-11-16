package com.oj.onlinejudge.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "提交代码请求")
public class SubmissionCreateRequest {

    @NotNull(message = "题目ID不能为空")
    private Long problemId;

    private Long homeworkId;

    @NotNull(message = "语言ID不能为空")
    private Integer languageId;

    @NotBlank(message = "源代码不能为空")
    private String sourceCode;
}
