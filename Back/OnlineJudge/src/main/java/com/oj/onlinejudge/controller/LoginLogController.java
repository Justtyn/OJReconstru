package com.oj.onlinejudge.controller;

// 登录日志模块 REST 控制器：记录用户登录/登出行为
// 支持按角色、用户ID过滤查询，提供增删改查接口

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.LoginLog;
import com.oj.onlinejudge.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login-logs")
@RequiredArgsConstructor
@Tag(name = "登录日志管理", description = "登录日志的查询与维护")
public class LoginLogController {

    private final LoginLogService loginLogService;

    /**
     * 分页查询登录日志（可按角色、用户ID筛选）
     */
    @GetMapping
    @Operation(summary = "分页列表", description = "分页获取登录日志，可按角色、用户ID过滤")
    @Parameters({
        @Parameter(name = "page", description = "页码(从1开始)", required = false),
        @Parameter(name = "size", description = "每页数量", required = false),
        @Parameter(name = "role", description = "角色：admin/teacher/student", required = false),
        @Parameter(name = "userId", description = "用户ID", required = false)
    })
    public ApiResponse<Page<LoginLog>> list(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) String role,
                                            @RequestParam(required = false) Long userId) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        if (role != null && !role.isEmpty()) wrapper.eq(LoginLog::getRole, role);
        if (userId != null) wrapper.eq(LoginLog::getUserId, userId);
        Page<LoginLog> p = loginLogService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(p);
    }

    /** 查询单条日志 */
    @GetMapping("/{id}")
    @Operation(summary = "日志详情", description = "根据ID获取登录日志")
    public ApiResponse<LoginLog> get(@Parameter(description = "日志ID") @PathVariable Long id) {
        LoginLog log = loginLogService.getById(id);
        return log == null ? ApiResponse.failure(404, "记录不存在") : ApiResponse.success(log);
    }

    /** 新增日志 */
    @PostMapping
    @Operation(summary = "新增日志", description = "新增一条登录日志记录")
    public ApiResponse<LoginLog> create(@RequestBody LoginLog body) {
        body.setId(null);
        boolean ok = loginLogService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /** 更新日志 */
    @PutMapping("/{id}")
    @Operation(summary = "更新日志", description = "根据ID更新登录日志")
    public ApiResponse<LoginLog> update(@Parameter(description = "日志ID") @PathVariable Long id,
                                        @RequestBody LoginLog body) {
        body.setId(id);
        boolean ok = loginLogService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "记录不存在");
    }

    /** 删除日志 */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除日志", description = "根据ID删除登录日志")
    public ApiResponse<Void> delete(@Parameter(description = "日志ID") @PathVariable Long id) {
        boolean ok = loginLogService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "记录不存在");
    }
}
