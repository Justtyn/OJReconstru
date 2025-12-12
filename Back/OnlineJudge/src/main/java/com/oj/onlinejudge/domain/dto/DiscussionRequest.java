package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "讨论创建/更新请求")
public class DiscussionRequest {

    @NotBlank(groups = CreateGroup.class, message = "标题不能为空")
    private String title;

    @NotBlank(groups = CreateGroup.class, message = "内容不能为空")
    private String content;

    private Long problemId;

    private Boolean isActive;

    @Schema(description = "管理员代学生发布时指定学生ID，仅创建时使用")
    private Long authorId;
}
