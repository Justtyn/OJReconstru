package com.oj.onlinejudge.controller;

// 教师模块 REST 控制器：提供教师的增删改查接口
// - GET /api/teachers 分页列表
// - GET /api/teachers/{id} 详情
// - POST /api/teachers 创建（对密码进行加密存储）
// - PUT /api/teachers/{id} 更新（如未提供密码保持旧值，提供则加密）
// - DELETE /api/teachers/{id} 删除

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.TeacherUpsertRequest;
import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Teacher;
import com.oj.onlinejudge.service.TeacherService;
import com.oj.onlinejudge.security.PasswordService;
import com.oj.onlinejudge.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.oj.onlinejudge.security.AuthenticatedUser;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final PasswordService passwordService;

    /**
     * 分页查询教师列表
     */
    @Operation(summary = "教师-分页列表")
    @GetMapping
    public ApiResponse<Page<Teacher>> list(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "用户名/姓名/邮箱关键字") @RequestParam(required = false) String keyword) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Teacher::getUsername, keyword)
                    .or()
                    .like(Teacher::getName, keyword)
                    .or()
                    .like(Teacher::getEmail, keyword));
        }
        Page<Teacher> p = teacherService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(p);
    }

    /**
     * 根据ID查询教师详情
     */
    @Operation(summary = "教师-详情")
    @GetMapping("/{id}")
    public ApiResponse<Teacher> get(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "教师ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Teacher teacher = teacherService.getById(id);
        if (teacher == null) {
            throw ApiException.notFound("教师不存在");
        }
        return ApiResponse.success(teacher);
    }

    /**
     * 新增教师（如包含明文密码，将进行哈希存储）
     */
    @Operation(summary = "教师-创建")
    @PostMapping
    public ApiResponse<Teacher> create(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Validated(CreateGroup.class) @RequestBody TeacherUpsertRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Teacher body = new Teacher();
        BeanUtils.copyProperties(request, body);
        body.setId(null);
        // 强制要求密码必填
        if (!org.springframework.util.StringUtils.hasText(body.getPassword())) {
            throw ApiException.badRequest("密码不能为空");
        }
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordService.encode(body.getPassword()));
        }
        boolean ok = teacherService.save(body);
        if (!ok) {
            throw ApiException.internal("创建失败");
        }
        return ApiResponse.success("创建成功", body);
    }

    /**
     * 更新教师（未提供 password 时保留原密码）
     */
    @Operation(summary = "教师-更新")
    @PutMapping("/{id}")
    public ApiResponse<Teacher> update(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "教师ID") @PathVariable Long id,
            @Validated(UpdateGroup.class) @RequestBody TeacherUpsertRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Teacher existing = teacherService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("教师不存在");
        }
        if (StringUtils.hasText(request.getPassword())) {
            existing.setPassword(passwordService.encode(request.getPassword()));
        }
        copyNonNullProperties(request, existing, "id", "password");
        existing.setId(id);
        boolean ok = teacherService.updateById(existing);
        if (!ok) {
            throw ApiException.notFound("教师不存在");
        }
        Teacher updated = teacherService.getById(id);
        return ApiResponse.success("更新成功", updated);
    }

    /**
     * 删除教师
     */
    @Operation(summary = "教师-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "教师ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        boolean ok = teacherService.removeById(id);
        if (!ok) {
            throw ApiException.notFound("教师不存在");
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
