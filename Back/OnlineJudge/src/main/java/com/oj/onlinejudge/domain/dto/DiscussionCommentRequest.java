package com.oj.onlinejudge.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "讨论评论请求体")
public class DiscussionCommentRequest {

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
