package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "班级成员创建/更新请求")
public class ClassesMemberRequest {

    @NotNull(groups = CreateGroup.class, message = "班级ID不能为空")
    private Long classId;

    @NotBlank(groups = CreateGroup.class, message = "成员类型不能为空")
    private String memberType;

    private Long studentId;

    private Long teacherId;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;
}
