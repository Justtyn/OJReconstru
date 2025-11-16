package com.oj.onlinejudge.domain.dto;

import javax.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "学生加入班级请求")
public class StudentJoinClassRequest {

    @NotBlank(message = "班级邀请码不能为空")
    @Schema(description = "班级邀请码", example = "CODEA123")
    private String code;
}
