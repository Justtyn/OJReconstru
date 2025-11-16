package com.oj.onlinejudge.controller;

import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.StudentJoinClassRequest;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/classes")
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentService studentService;

    @Operation(summary = "学生加入班级", description = "学生通过邀请码加入班级，仅允许加入一个班级")
    @PostMapping("/join")
    public ApiResponse<Classes> joinClass(
            @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false)
            @Parameter(description = "当前认证学生") AuthenticatedUser current,
            @Validated @RequestBody StudentJoinClassRequest request) {
        ensureStudent(current);
        Classes joined = studentService.joinClass(current.getUserId(), request.getCode());
        return ApiResponse.success("加入班级成功", joined);
    }

    @Operation(summary = "学生当前班级", description = "返回学生已加入的班级信息")
    @GetMapping
    public ApiResponse<Classes> currentClass(
            @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false)
            @Parameter(description = "当前认证学生") AuthenticatedUser current) {
        ensureStudent(current);
        Classes classes = studentService.getJoinedClass(current.getUserId());
        return ApiResponse.success(classes);
    }

    private void ensureStudent(AuthenticatedUser current) {
        if (current == null || !"student".equalsIgnoreCase(current.getRole())) {
            throw ApiException.unauthorized("仅学生可访问该接口");
        }
    }
}
