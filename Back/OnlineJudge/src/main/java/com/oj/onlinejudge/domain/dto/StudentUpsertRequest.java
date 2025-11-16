package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "学生创建/更新请求")
public class StudentUpsertRequest {

    @NotBlank(groups = CreateGroup.class, message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64")
    private String username;

    @NotBlank(groups = CreateGroup.class, message = "密码不能为空")
    private String password;

    private String name;

    private String sex;

    private LocalDate birth;

    private String phone;

    @Email(message = "邮箱格式不正确", groups = {CreateGroup.class, UpdateGroup.class})
    private String email;

    private String avatar;

    private String background;

    private String school;

    private Integer score;

    private String bio;
}
