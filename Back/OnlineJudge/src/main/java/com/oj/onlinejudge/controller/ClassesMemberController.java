package com.oj.onlinejudge.controller;

// 班级成员模块 REST 控制器：维护班级与成员（学生/教师）的关系
// 支持按班级ID过滤、分页查询、详情、新增、更新、删除

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.ClassesMember;
import com.oj.onlinejudge.service.ClassesMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.oj.onlinejudge.security.AuthenticatedUser;

@RestController
@RequestMapping("/api/classes-members")
@RequiredArgsConstructor
public class ClassesMemberController {

    private final ClassesMemberService classesMemberService;

    /**
     * 分页查询班级成员（可选按classId过滤）
     */
    @Operation(summary = "班级成员-分页列表")
    @GetMapping
    public ApiResponse<Page<ClassesMember>> list(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "班级ID过滤") @RequestParam(required = false) Long classId) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        LambdaQueryWrapper<ClassesMember> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(ClassesMember::getClassId, classId);
        }
        Page<ClassesMember> p = classesMemberService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(p);
    }

    /**
     * 查询详情
     */
    @Operation(summary = "班级成员-详情")
    @GetMapping("/{id}")
    public ApiResponse<ClassesMember> get(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "记录ID") @PathVariable Long id) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        ClassesMember cm = classesMemberService.getById(id);
        return cm == null ? ApiResponse.failure(404, "记录不存在") : ApiResponse.success(cm);
    }

    /**
     * 新增记录
     */
    @Operation(summary = "班级成员-创建")
    @PostMapping
    public ApiResponse<ClassesMember> create(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "班级成员实体") @RequestBody ClassesMember body) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        body.setId(null);
        boolean ok = classesMemberService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /**
     * 更新记录
     */
    @Operation(summary = "班级成员-更新")
    @PutMapping("/{id}")
    public ApiResponse<ClassesMember> update(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "记录ID") @PathVariable Long id,
            @Parameter(description = "班级成员实体") @RequestBody ClassesMember body) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        body.setId(id);
        boolean ok = classesMemberService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "记录不存在");
    }

    /**
     * 删除记录
     */
    @Operation(summary = "班级成员-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "记录ID") @PathVariable Long id) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        boolean ok = classesMemberService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "记录不存在");
    }
}
