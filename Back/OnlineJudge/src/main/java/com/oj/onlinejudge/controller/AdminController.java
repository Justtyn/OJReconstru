package com.oj.onlinejudge.controller;

// 管理员模块 REST 控制器，提供管理员的增删改查接口
// - 列表查询：分页返回管理员列表
// - 详情查询：根据ID返回管理员信息
// - 创建：新增管理员
// - 更新：根据ID更新管理员
// - 删除：根据ID删除管理员

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.Admin;
import com.oj.onlinejudge.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Tag(name = "管理员管理", description = "管理员的增删改查接口")
public class AdminController {

    private final AdminService adminService;

    /**
     * 分页查询管理员列表
     */
    @GetMapping
    @Operation(summary = "分页列表", description = "分页获取管理员列表")
    @Parameters({
            @Parameter(name = "page", description = "页码(从1开始)", required = false),
            @Parameter(name = "size", description = "每页数量", required = false)
    })
    public ApiResponse<Page<Admin>> list(@RequestParam(defaultValue = "1") long page,
                                         @RequestParam(defaultValue = "10") long size) {
        Page<Admin> p = adminService.page(new Page<>(page, size));
        return ApiResponse.success(p);
    }

    /**
     * 根据ID查询管理员详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "管理员详情", description = "根据ID获取管理员信息")
    public ApiResponse<Admin> get(@Parameter(description = "管理员ID") @PathVariable Long id) {
        Admin admin = adminService.getById(id);
        return admin == null ? ApiResponse.failure(404, "管理员不存在") : ApiResponse.success(admin);
    }

    /**
     * 新增管理员
     */
    @PostMapping
    @Operation(summary = "创建管理员", description = "新增一个管理员账号")
    public ApiResponse<Admin> create(@RequestBody Admin body) {
        body.setId(null);
        boolean ok = adminService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /**
     * 更新管理员
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新管理员", description = "根据ID更新管理员信息")
    public ApiResponse<Admin> update(@Parameter(description = "管理员ID") @PathVariable Long id,
                                     @RequestBody Admin body) {
        body.setId(id);
        boolean ok = adminService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "管理员不存在");
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除管理员", description = "根据ID删除管理员")
    public ApiResponse<Void> delete(@Parameter(description = "管理员ID") @PathVariable Long id) {
        boolean ok = adminService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "管理员不存在");
    }
}
