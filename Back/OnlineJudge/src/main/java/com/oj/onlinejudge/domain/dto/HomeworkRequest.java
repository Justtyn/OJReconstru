package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "作业创建/更新请求")
public class HomeworkRequest {

    @NotBlank(groups = CreateGroup.class, message = "作业标题不能为空")
    private String title;

    @NotNull(groups = CreateGroup.class, message = "班级ID不能为空")
    private Long classId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private Boolean isActive;

    @Size(min = 1, groups = CreateGroup.class, message = "至少需要选择一题")
    private List<Long> problemIds;
}
