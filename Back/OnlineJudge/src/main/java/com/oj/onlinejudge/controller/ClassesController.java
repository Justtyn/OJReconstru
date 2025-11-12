package com.oj.onlinejudge.controller;

// 班级模块 REST 控制器：提供班级的增删改查接口
// - GET /api/classes 分页列表
// - GET /api/classes/{id} 详情
// - POST /api/classes 创建
// - PUT /api/classes/{id} 更新
// - DELETE /api/classes/{id} 删除

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.service.ClassesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "班级管理", description = "班级的增删改查接口")
public class ClassesController {

    private final ClassesService classesService;

    /** 分页查询班级列表 */
    @GetMapping
    @Operation(summary = "分页列表", description = "分页获取班级列表")
    @Parameters({
        @Parameter(name = "page", description = "页码(从1开始)", required = false),
        @Parameter(name = "size", description = "每页数量", required = false)
    })
    public ApiResponse<Page<Classes>> list(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size) {
        Page<Classes> p = classesService.page(new Page<>(page, size));
        return ApiResponse.success(p);
    }

    /** 根据ID查询班级详情 */
    @GetMapping("/{id}")
    @Operation(summary = "班级详情", description = "根据ID获取班级信息")
    public ApiResponse<Classes> get(@Parameter(description = "班级ID") @PathVariable Long id) {
        Classes c = classesService.getById(id);
        return c == null ? ApiResponse.failure(404, "班级不存在") : ApiResponse.success(c);
    }

    /** 新增班级 */
    @PostMapping
    @Operation(summary = "创建班级", description = "新增一个班级")
    public ApiResponse<Classes> create(@RequestBody Classes body) {
        body.setId(null);
        boolean ok = classesService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /** 更新班级 */
    @PutMapping("/{id}")
    @Operation(summary = "更新班级", description = "根据ID更新班级信息")
    public ApiResponse<Classes> update(@Parameter(description = "班级ID") @PathVariable Long id, @RequestBody Classes body) {
        body.setId(id);
        boolean ok = classesService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "班级不存在");
    }

    /** 删除班级 */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级", description = "根据ID删除班级")
    public ApiResponse<Void> delete(@Parameter(description = "班级ID") @PathVariable Long id) {
        boolean ok = classesService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "班级不存在");
    }
}
