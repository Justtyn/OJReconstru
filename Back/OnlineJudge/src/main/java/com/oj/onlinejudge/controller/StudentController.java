package com.oj.onlinejudge.controller;

// 学生模块 REST 控制器：提供学生的增删改查接口
// 注意：创建/更新时如果传入 password 字段，则会进行密码哈希；不传则更新时保持原密码不变

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.StudentUpsertRequest;
import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.PasswordService;
import com.oj.onlinejudge.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestAttribute;
import com.oj.onlinejudge.security.AuthenticatedUser;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final PasswordService passwordService;

    /**
     * 分页查询学生列表，支持 username/email 模糊搜索
     */
    @Operation(summary = "学生-分页列表")
    @GetMapping
    public ApiResponse<Page<Student>> list(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "用户名/姓名/邮箱关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "按用户名模糊搜索") @RequestParam(required = false) String username,
            @Parameter(description = "按邮箱模糊搜索") @RequestParam(required = false) String email,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long page) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Student::getUsername, keyword)
                    .or()
                    .like(Student::getName, keyword)
                    .or()
                    .like(Student::getEmail, keyword));
        }
        if (StringUtils.hasText(username)) {
            wrapper.like(Student::getUsername, username);
        }
        if (StringUtils.hasText(email)) {
            wrapper.like(Student::getEmail, email);
        }
        Page<Student> p = studentService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(p);
    }

    /**
     * 根据ID查询学生详情
     */
    @Operation(summary = "学生-详情")
    @GetMapping("/{id}")
    public ApiResponse<Student> get(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "学生ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Student student = studentService.getById(id);
        if (student == null) {
            throw ApiException.notFound("学生不存在");
        }
        return ApiResponse.success(student);
    }

    /**
     * 新增学生（如包含明文密码，将进行哈希存储）
     */
    @Operation(summary = "学生-创建", description = "创建新学生并可选设置初始密码")
    @PostMapping
    public ApiResponse<Student> create(
            @Parameter(description = "当前认证用户", required = true) @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "学生信息") @Validated(CreateGroup.class) @RequestBody StudentUpsertRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Student body = new Student();
        BeanUtils.copyProperties(request, body);
        body.setId(null);
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordService.encode(body.getPassword()));
        }
        boolean ok = studentService.save(body);
        if (!ok) {
            throw ApiException.internal("创建失败");
        }
        return ApiResponse.success("创建成功", body);
    }

    /**
     * 更新学生（未提供 password 时保留原密码）
     */
    @Operation(summary = "学生-更新", description = "根据ID更新学生信息，密码为空则保持原值")
    @PutMapping("/{id}")
    public ApiResponse<Student> update(
            @Parameter(description = "当前认证用户", required = true) @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "学生ID", example = "123") @PathVariable Long id,
            @Parameter(description = "学生信息") @Validated(UpdateGroup.class) @RequestBody StudentUpsertRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Student existing = studentService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("学生不存在");
        }
        if (StringUtils.hasText(request.getPassword())) {
            existing.setPassword(passwordService.encode(request.getPassword()));
        }
        copyNonNullProperties(request, existing, "id", "password");
        existing.setId(id);
        boolean ok = studentService.updateById(existing);
        if (!ok) {
            throw ApiException.notFound("学生不存在");
        }
        Student updated = studentService.getById(id);
        return ApiResponse.success("更新成功", updated);
    }

    /**
     * 删除学生
     */
    @Operation(summary = "学生-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "学生ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        boolean ok = studentService.removeById(id);
        if (!ok) {
            throw ApiException.notFound("学生不存在");
        }
        return ApiResponse.success(null);
    }

    /**
     * 复制非空字段，避免把空值覆盖数据库已有值
     */
    private void copyNonNullProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanWrapper src = new org.springframework.beans.BeanWrapperImpl(source);
        java.util.Set<String> ignore = new java.util.HashSet<>();
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object val = src.getPropertyValue(pd.getName());
            if (val == null) {
                ignore.add(pd.getName());
            }
        }
        if (ignoreProperties != null && ignoreProperties.length > 0) {
            java.util.Collections.addAll(ignore, ignoreProperties);
        }
        BeanUtils.copyProperties(source, target, ignore.toArray(new String[0]));
    }
}
