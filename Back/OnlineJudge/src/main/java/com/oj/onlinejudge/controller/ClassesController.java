package com.oj.onlinejudge.controller;

// 班级模块 REST 控制器：提供班级的增删改查接口
// - GET /api/classes 分页列表
// - GET /api/classes/{id} 详情
// - POST /api/classes 创建
// - PUT /api/classes/{id} 更新
// - DELETE /api/classes/{id} 删除

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.ClassesRequest;
import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.domain.entity.ClassesMember;
import com.oj.onlinejudge.service.ClassesMemberService;
import com.oj.onlinejudge.service.ClassesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassesController {

    private final ClassesService classesService;
    private final ClassesMemberService classesMemberService;

    /**
     * 分页查询班级列表
     */
    @Operation(summary = "班级-分页列表")
    @GetMapping
    public ApiResponse<Page<Classes>> list(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "名称/描述/邀请码关键字") @RequestParam(required = false) String keyword) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Page<Classes> p;
        if (isTeacher(current)) {
            LambdaQueryWrapper<Classes> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Classes::getCreatorId, current.getUserId());
            if (StringUtils.hasText(keyword)) {
                wrapper.and(w -> w.like(Classes::getName, keyword)
                        .or()
                        .like(Classes::getDescription, keyword)
                        .or()
                        .like(Classes::getCode, keyword));
            }
            p = classesService.page(new Page<>(page, size), wrapper);
        } else {
            LambdaQueryWrapper<Classes> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(keyword)) {
                wrapper.and(w -> w.like(Classes::getName, keyword)
                        .or()
                        .like(Classes::getDescription, keyword)
                        .or()
                        .like(Classes::getCode, keyword));
            }
            p = classesService.page(new Page<>(page, size), wrapper);
        }
        return ApiResponse.success(p);
    }

    /**
     * 根据ID查询班级详情
     */
    @Operation(summary = "班级-详情")
    @GetMapping("/{id}")
    public ApiResponse<Classes> get(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "班级ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Classes c = classesService.getById(id);
        if (c == null) {
            throw ApiException.notFound("班级不存在");
        }
        return ApiResponse.success(c);
    }

    /**
     * 新增班级
     */
    @Operation(summary = "班级-创建")
    @PostMapping
    public ApiResponse<Classes> create(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Validated(CreateGroup.class) @RequestBody ClassesRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Classes body = new Classes();
        BeanUtils.copyProperties(request, body);
        body.setId(null);
        if (isTeacher(current)) {
            body.setCreatorId(current.getUserId());
        }
        ensureTeacherOrAdmin(current);
        boolean ok = classesService.save(body);
        if (!ok) {
            throw ApiException.internal("创建失败");
        }
        if (isTeacher(current)) {
            ensureTeacherMembership(current, body);
        }
        return ApiResponse.success("创建成功", body);
    }

    /**
     * 更新班级
     */
    @Operation(summary = "班级-更新")
    @PutMapping("/{id}")
    public ApiResponse<Classes> update(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "班级ID") @PathVariable Long id,
            @Validated(UpdateGroup.class) @RequestBody ClassesRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Classes existing = classesService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("班级不存在");
        }
        ensureCanManageClass(current, existing);
        Classes body = new Classes();
        BeanUtils.copyProperties(request, body);
        body.setId(id);
        if (body.getCreatorId() == null) {
            body.setCreatorId(existing.getCreatorId());
        }
        boolean ok = classesService.updateById(body);
        if (!ok) {
            throw ApiException.notFound("班级不存在");
        }
        return ApiResponse.success("更新成功", body);
    }

    /**
     * 删除班级
     */
    @Operation(summary = "班级-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "班级ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Classes existing = classesService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("班级不存在");
        }
        ensureCanManageClass(current, existing);
        boolean ok = classesService.removeById(id);
        if (!ok) {
            throw ApiException.notFound("班级不存在");
        }
        return ApiResponse.success(null);
    }

    private boolean isTeacher(AuthenticatedUser current) {
        return current != null && "teacher".equalsIgnoreCase(current.getRole());
    }

    private boolean isAdmin(AuthenticatedUser current) {
        return current != null && "admin".equalsIgnoreCase(current.getRole());
    }

    private void ensureTeacherOrAdmin(AuthenticatedUser current) {
        if (!(isTeacher(current) || isAdmin(current))) {
            throw ApiException.forbidden("仅教师或管理员可执行此操作");
        }
    }

    private void ensureCanManageClass(AuthenticatedUser current, Classes target) {
        ensureTeacherOrAdmin(current);
        if (isTeacher(current)) {
            if (target.getCreatorId() != null && !target.getCreatorId().equals(current.getUserId())) {
                throw ApiException.forbidden("只能管理自己创建的班级");
            }
        }
    }

    private void ensureTeacherMembership(AuthenticatedUser current, Classes clazz) {
        boolean exists = classesMemberService.lambdaQuery()
                .eq(ClassesMember::getClassId, clazz.getId())
                .eq(ClassesMember::getMemberType, "teacher")
                .eq(ClassesMember::getTeacherId, current.getUserId())
                .count() > 0;
        if (!exists) {
            ClassesMember member = new ClassesMember();
            member.setClassId(clazz.getId());
            member.setMemberType("teacher");
            member.setTeacherId(current.getUserId());
            member.setJoinedAt(java.time.LocalDateTime.now());
            classesMemberService.save(member);
        }
    }
}
