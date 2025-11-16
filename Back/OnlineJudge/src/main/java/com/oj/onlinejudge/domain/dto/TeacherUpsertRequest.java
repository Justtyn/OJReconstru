package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "教师创建/更新请求")
public class TeacherUpsertRequest {

    @NotBlank(groups = CreateGroup.class, message = "用户名不能为空")
    private String username;

    @NotBlank(groups = CreateGroup.class, message = "密码不能为空")
    private String password;

    private String name;

    private String sex;

    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String avatar;

    private String title;
}
