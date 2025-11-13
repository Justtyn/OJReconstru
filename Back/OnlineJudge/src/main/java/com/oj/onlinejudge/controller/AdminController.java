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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 分页查询管理员列表
     * @param page 第几页（从1开始）
     * @param size 每页数量
     * @return 分页数据
     */
    @Operation(summary = "管理员-分页列表")
    @GetMapping
    public ApiResponse<Page<Admin>> list(@Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") long page,
                                         @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size) {
        Page<Admin> p = adminService.page(new Page<>(page, size));
        return ApiResponse.success(p);
    }

    /**
     * 根据ID查询管理员详情
     * @param id 管理员ID
     * @return 管理员信息
     */
    @Operation(summary = "管理员-详情")
    @GetMapping("/{id}")
    public ApiResponse<Admin> get(@Parameter(description = "管理员ID") @PathVariable Long id) {
        Admin admin = adminService.getById(id);
        return admin == null ? ApiResponse.failure(404, "管理员不存在") : ApiResponse.success(admin);
    }

    /**
     * 新增管理员
     * @param body 管理员实体
     * @return 新建结果
     */
    @Operation(summary = "管理员-创建")
    @PostMapping
    public ApiResponse<Admin> create(@Parameter(description = "管理员实体") @RequestBody Admin body) {
        body.setId(null);
        boolean ok = adminService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /**
     * 更新管理员
     * @param id 管理员ID
     * @param body 管理员实体（部分/全部字段）
     * @return 更新结果
     */
    @Operation(summary = "管理员-更新")
    @PutMapping("/{id}")
    public ApiResponse<Admin> update(@Parameter(description = "管理员ID") @PathVariable Long id,
                                     @Parameter(description = "管理员实体") @RequestBody Admin body) {
        body.setId(id);
        boolean ok = adminService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "管理员不存在");
    }

    /**
     * 删除管理员
     * @param id 管理员ID
     * @return 删除结果
     */
    @Operation(summary = "管理员-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "管理员ID") @PathVariable Long id) {
        boolean ok = adminService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "管理员不存在");
    }
}
