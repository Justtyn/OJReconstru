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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassesController {

    private final ClassesService classesService;

    /** 分页查询班级列表 */
    @Operation(summary = "班级-分页列表")
    @GetMapping
    public ApiResponse<Page<Classes>> list(@Parameter(description = "页码") @RequestParam(defaultValue = "1") long page,
                                           @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size) {
        Page<Classes> p = classesService.page(new Page<>(page, size));
        return ApiResponse.success(p);
    }

    /** 根据ID查询班级详情 */
    @Operation(summary = "班级-详情")
    @GetMapping("/{id}")
    public ApiResponse<Classes> get(@Parameter(description = "班级ID") @PathVariable Long id) {
        Classes c = classesService.getById(id);
        return c == null ? ApiResponse.failure(404, "班级不存在") : ApiResponse.success(c);
    }

    /** 新增班级 */
    @Operation(summary = "班级-创建")
    @PostMapping
    public ApiResponse<Classes> create(@Parameter(description = "班级实体") @RequestBody Classes body) {
        body.setId(null);
        boolean ok = classesService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /** 更新班级 */
    @Operation(summary = "班级-更新")
    @PutMapping("/{id}")
    public ApiResponse<Classes> update(@Parameter(description = "班级ID") @PathVariable Long id,
                                       @Parameter(description = "班级实体") @RequestBody Classes body) {
        body.setId(id);
        boolean ok = classesService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "班级不存在");
    }

    /** 删除班级 */
    @Operation(summary = "班级-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "班级ID") @PathVariable Long id) {
        boolean ok = classesService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "班级不存在");
    }
}
