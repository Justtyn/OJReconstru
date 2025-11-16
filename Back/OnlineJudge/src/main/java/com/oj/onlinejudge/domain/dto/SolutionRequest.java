package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "题解创建/更新请求")
public class SolutionRequest {

    private Long problemId;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "标题不能为空")
    private String title;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "内容不能为空")
    private String content;

    @Schema(description = "语言")
    private String language;

    @Schema(description = "是否启用")
    private Boolean isActive;
}
