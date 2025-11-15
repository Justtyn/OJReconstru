package com.oj.onlinejudge.controller;

// 登录日志模块 REST 控制器：记录用户登录/登出行为
// 支持按角色、用户ID过滤查询，提供增删改查接口

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.LoginLog;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.oj.onlinejudge.security.AuthenticatedUser;

@RestController
@RequestMapping("/api/login-logs")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    /**
     * 分页查询登录日志（可按角色、用户ID筛选）
     */
    @Operation(summary = "登录日志-分页列表")
    @GetMapping
    public ApiResponse<Page<LoginLog>> list(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "角色过滤") @RequestParam(required = false) String role,
            @Parameter(description = "用户ID过滤") @RequestParam(required = false) Long userId) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        if (role != null && !role.isEmpty()) wrapper.eq(LoginLog::getRole, role);
        if (userId != null) wrapper.eq(LoginLog::getUserId, userId);
        Page<LoginLog> p = loginLogService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(p);
    }

    /**
     * 查询单条日志
     */
    @Operation(summary = "登录日志-详情")
    @GetMapping("/{id}")
    public ApiResponse<LoginLog> get(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "日志ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        LoginLog log = loginLogService.getById(id);
        if (log == null) {
            throw ApiException.notFound("记录不存在");
        }
        return ApiResponse.success(log);
    }

    /**
     * 新增日志
     */
    @Operation(summary = "登录日志-创建")
    @PostMapping
    public ApiResponse<LoginLog> create(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "日志实体") @RequestBody LoginLog body) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        body.setId(null);
        boolean ok = loginLogService.save(body);
        if (!ok) {
            throw ApiException.internal("创建失败");
        }
        return ApiResponse.success("创建成功", body);
    }

    /**
     * 更新日志
     */
    @Operation(summary = "登录日志-更新")
    @PutMapping("/{id}")
    public ApiResponse<LoginLog> update(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "日志ID") @PathVariable Long id,
            @Parameter(description = "日志实体") @RequestBody LoginLog body) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        body.setId(id);
        boolean ok = loginLogService.updateById(body);
        if (!ok) {
            throw ApiException.notFound("记录不存在");
        }
        return ApiResponse.success("更新成功", body);
    }

    /**
     * 删除日志
     */
    @Operation(summary = "登录日志-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "日志ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        boolean ok = loginLogService.removeById(id);
        if (!ok) {
            throw ApiException.notFound("记录不存在");
        }
        return ApiResponse.success(null);
    }
}
