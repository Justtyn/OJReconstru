package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "班级创建/更新请求")
public class ClassesRequest {

    @NotBlank(groups = CreateGroup.class, message = "班级名称不能为空")
    private String name;

    private Long creatorId;

    private String code;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}
