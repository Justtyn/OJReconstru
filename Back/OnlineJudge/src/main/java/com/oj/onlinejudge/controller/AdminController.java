package com.oj.onlinejudge.controller;

// 管理员模块 REST 控制器，提供管理员的增删改查接口
// - 列表查询：分页返回管理员列表
// - 详情查询：根据ID返回管理员信息
// - 创建：新增管理员（对密码进行加密存储）
// - 更新：根据ID更新管理员（若未提供密码则保持旧值，若提供则加密）

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.AdminUpsertRequest;
import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Admin;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.service.AdminService;
import com.oj.onlinejudge.security.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.oj.onlinejudge.security.AuthenticatedUser;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final PasswordService passwordService;

    /**
     * 分页查询管理员列表
     *
     * @param page 第几页（从1开始）
     * @param size 每页数量
     * @return 分页数据
     */
    @Operation(summary = "管理员-分页列表")
    @GetMapping
    public ApiResponse<Page<Admin>> list(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Page<Admin> p = adminService.page(new Page<>(page, size));
        return ApiResponse.success(p);
    }

    /**
     * 根据ID查询管理员详情
     *
     * @param id 管理员ID
     * @return 管理员信息
     */
    @Operation(summary = "管理员-详情")
    @GetMapping("/{id}")
    public ApiResponse<Admin> get(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "管理员ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Admin admin = adminService.getById(id);
        if (admin == null) {
            throw ApiException.notFound("管理员不存在");
        }
        return ApiResponse.success(admin);
    }

    /**
     * 新增管理员（如包含明文密码，将进行哈希存储）
     *
     * @param body 管理员实体
     * @return 新建结果
     */
    @Operation(summary = "管理员-创建")
    @PostMapping
    public ApiResponse<Admin> create(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Validated(CreateGroup.class) @RequestBody AdminUpsertRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Admin body = new Admin();
        BeanUtils.copyProperties(request, body);
        body.setId(null);
        // 强制要求密码必填
        if (!org.springframework.util.StringUtils.hasText(body.getPassword())) {
            throw ApiException.badRequest("密码不能为空");
        }
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordService.encode(body.getPassword()));
        }
        boolean ok = adminService.save(body);
        if (!ok) {
            throw ApiException.internal("创建失败");
        }
        return ApiResponse.success("创建成功", body);
    }

    /**
     * 更新管理员（未提供 password 时保留原密码）
     *
     * @param id   管理员ID
     * @param body 管理员实体（部分/全部字段）
     * @return 更新结果
     */
    @Operation(summary = "管理员-更新")
    @PutMapping("/{id}")
    public ApiResponse<Admin> update(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "管理员ID") @PathVariable Long id,
            @Validated(UpdateGroup.class) @RequestBody AdminUpsertRequest request) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Admin body = new Admin();
        BeanUtils.copyProperties(request, body);
        body.setId(id);
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordService.encode(body.getPassword()));
        } else {
            Admin old = adminService.getById(id);
            if (old == null) {
                throw ApiException.notFound("管理员不存在");
            }
            body.setPassword(old.getPassword());
        }
        boolean ok = adminService.updateById(body);
        if (!ok) {
            throw ApiException.notFound("管理员不存在");
        }
        return ApiResponse.success("更新成功", body);
    }

    /**
     * 删除管理员
     *
     * @param id 管理员ID
     * @return 删除结果
     */
    @Operation(summary = "管理员-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "管理员ID") @PathVariable Long id) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        boolean ok = adminService.removeById(id);
        if (!ok) {
            throw ApiException.notFound("管理员不存在");
        }
        return ApiResponse.success(null);
    }
}
