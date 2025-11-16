package com.oj.onlinejudge.controller;

// 班级成员模块 REST 控制器：维护班级与成员（学生/教师）的关系
// 支持按班级ID过滤、分页查询、详情、新增、更新、删除

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.ClassesMemberRequest;
import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.domain.entity.ClassesMember;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.ClassesMemberService;
import com.oj.onlinejudge.service.ClassesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes-members")
@RequiredArgsConstructor
public class ClassesMemberController {

    private final ClassesMemberService classesMemberService;
    private final ClassesService classesService;

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
            throw ApiException.unauthorized("未登录或Token失效");
        }
        requireTeacherOrAdmin(current);
        if (isTeacher(current)) {
            if (classId == null) {
                throw ApiException.badRequest("教师查询班级成员必须提供班级ID");
            }
            ensureTeacherOwnsClass(current, classId);
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
            throw ApiException.unauthorized("未登录或Token失效");
        }
        requireTeacherOrAdmin(current);
        ClassesMember cm = classesMemberService.getById(id);
        if (cm == null) {
            throw ApiException.notFound("记录不存在");
        }
        if (isTeacher(current)) {
            ensureTeacherOwnsClass(current, cm.getClassId());
        }
        return ApiResponse.success(cm);
    }

    /**
     * 新增记录
     */
    @Operation(summary = "班级成员-创建")
    @PostMapping
    public ApiResponse<ClassesMember> create(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Validated(CreateGroup.class) @RequestBody ClassesMemberRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        requireTeacherOrAdmin(current);
        if (request.getClassId() == null) {
            throw ApiException.badRequest("班级ID不能为空");
        }
        if (isTeacher(current)) {
            ensureTeacherOwnsClass(current, request.getClassId());
        }
        ClassesMember body = new ClassesMember();
        BeanUtils.copyProperties(request, body);
        body.setId(null);
        boolean ok = classesMemberService.save(body);
        if (!ok) {
            throw ApiException.internal("创建失败");
        }
        return ApiResponse.success("创建成功", body);
    }

    /**
     * 更新记录
     */
    @Operation(summary = "班级成员-更新")
    @PutMapping("/{id}")
    public ApiResponse<ClassesMember> update(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "记录ID") @PathVariable Long id,
            @Validated(UpdateGroup.class) @RequestBody ClassesMemberRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        requireTeacherOrAdmin(current);
        ClassesMember existing = classesMemberService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("记录不存在");
        }
        if (isTeacher(current)) {
            ensureTeacherOwnsClass(current, existing.getClassId());
        }
        ClassesMember body = new ClassesMember();
        BeanUtils.copyProperties(request, body);
        body.setId(id);
        if (body.getClassId() == null) {
            body.setClassId(existing.getClassId());
        } else if (isTeacher(current)) {
            ensureTeacherOwnsClass(current, body.getClassId());
        }
        boolean ok = classesMemberService.updateById(body);
        if (!ok) {
            throw ApiException.notFound("记录不存在");
        }
        return ApiResponse.success("更新成功", body);
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
            throw ApiException.unauthorized("未登录或Token失效");
        }
        requireTeacherOrAdmin(current);
        ClassesMember existing = classesMemberService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("记录不存在");
        }
        if (isTeacher(current)) {
            ensureTeacherOwnsClass(current, existing.getClassId());
        }
        boolean ok = classesMemberService.removeById(id);
        if (!ok) {
            throw ApiException.notFound("记录不存在");
        }
        return ApiResponse.success(null);
    }

    private boolean isTeacher(AuthenticatedUser current) {
        return current != null && "teacher".equalsIgnoreCase(current.getRole());
    }

    private boolean isAdmin(AuthenticatedUser current) {
        return current != null && "admin".equalsIgnoreCase(current.getRole());
    }

    private void requireTeacherOrAdmin(AuthenticatedUser current) {
        if (!(isTeacher(current) || isAdmin(current))) {
            throw ApiException.forbidden("仅教师或管理员可操作班级成员");
        }
    }

    private void ensureTeacherOwnsClass(AuthenticatedUser current, Long classId) {
        if (classId == null) {
            throw ApiException.badRequest("班级ID不能为空");
        }
        Classes target = classesService.getById(classId);
        if (target == null) {
            throw ApiException.notFound("班级不存在");
        }
        if (isTeacher(current)) {
            if (target.getCreatorId() != null && !target.getCreatorId().equals(current.getUserId())) {
                throw ApiException.forbidden("只能管理自己创建的班级");
            }
        }
    }
}
