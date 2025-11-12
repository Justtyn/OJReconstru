package com.oj.onlinejudge.controller;

// 教师模块 REST 控制器：提供教师的增删改查接口
// - GET /api/teachers 分页列表
// - GET /api/teachers/{id} 详情
// - POST /api/teachers 创建
// - PUT /api/teachers/{id} 更新
// - DELETE /api/teachers/{id} 删除

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.Teacher;
import com.oj.onlinejudge.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "教师管理", description = "教师的增删改查接口")
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * 分页查询教师列表
     */
    @GetMapping
    @Operation(summary = "分页列表", description = "分页获取教师列表")
    @Parameters({
        @Parameter(name = "page", description = "页码(从1开始)", required = false),
        @Parameter(name = "size", description = "每页数量", required = false)
    })
    public ApiResponse<Page<Teacher>> list(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size) {
        Page<Teacher> p = teacherService.page(new Page<>(page, size));
        return ApiResponse.success(p);
    }

    /**
     * 根据ID查询教师详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "教师详情", description = "根据ID获取教师信息")
    public ApiResponse<Teacher> get(@Parameter(description = "教师ID") @PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        return teacher == null ? ApiResponse.failure(404, "教师不存在") : ApiResponse.success(teacher);
    }

    /**
     * 新增教师
     */
    @PostMapping
    @Operation(summary = "创建教师", description = "新增一个教师账号")
    public ApiResponse<Teacher> create(@RequestBody Teacher body) {
        body.setId(null);
        boolean ok = teacherService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /**
     * 更新教师
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新教师", description = "根据ID更新教师信息")
    public ApiResponse<Teacher> update(@Parameter(description = "教师ID") @PathVariable Long id, @RequestBody Teacher body) {
        body.setId(id);
        boolean ok = teacherService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "教师不存在");
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除教师", description = "根据ID删除教师")
    public ApiResponse<Void> delete(@Parameter(description = "教师ID") @PathVariable Long id) {
        boolean ok = teacherService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "教师不存在");
    }
}
