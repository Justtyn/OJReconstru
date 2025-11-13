package com.oj.onlinejudge.controller;

// 教师模块 REST 控制器：提供教师的增删改查接口
// - GET /api/teachers 分页列表
// - GET /api/teachers/{id} 详情
// - POST /api/teachers 创建（对密码进行加密存储）
// - PUT /api/teachers/{id} 更新（如未提供密码保持旧值，提供则加密）
// - DELETE /api/teachers/{id} 删除

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.Teacher;
import com.oj.onlinejudge.service.TeacherService;
import com.oj.onlinejudge.security.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
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
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        Page<Teacher> p = teacherService.page(new Page<>(page, size));
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
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        Teacher teacher = teacherService.getById(id);
        return teacher == null ? ApiResponse.failure(404, "教师不存在") : ApiResponse.success(teacher);
    }

    /**
     * 新增教师（如包含明文密码，将进行哈希存储）
     */
    @Operation(summary = "教师-创建")
    @PostMapping
    public ApiResponse<Teacher> create(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "教师实体") @RequestBody Teacher body) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        body.setId(null);
        // 强制要求密码必填
        if (!org.springframework.util.StringUtils.hasText(body.getPassword())) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordService.encode(body.getPassword()));
        }
        boolean ok = teacherService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /**
     * 更新教师（未提供 password 时保留原密码）
     */
    @Operation(summary = "教师-更新")
    @PutMapping("/{id}")
    public ApiResponse<Teacher> update(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "教师ID") @PathVariable Long id,
            @Parameter(description = "教师实体") @RequestBody Teacher body) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        body.setId(id);
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordService.encode(body.getPassword()));
        } else {
            Teacher old = teacherService.getById(id);
            if (old == null) {
                return ApiResponse.failure(404, "教师不存在");
            }
            body.setPassword(old.getPassword());
        }
        boolean ok = teacherService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "教师不存在");
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
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        boolean ok = teacherService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "教师不存在");
    }
}
