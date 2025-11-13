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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * 分页查询教师列表
     */
    @Operation(summary = "教师-分页列表")
    @GetMapping
    public ApiResponse<Page<Teacher>> list(@Parameter(description = "页码") @RequestParam(defaultValue = "1") long page,
                                           @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size) {
        Page<Teacher> p = teacherService.page(new Page<>(page, size));
        return ApiResponse.success(p);
    }

    /**
     * 根据ID查询教师详情
     */
    @Operation(summary = "教师-详情")
    @GetMapping("/{id}")
    public ApiResponse<Teacher> get(@Parameter(description = "教师ID") @PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        return teacher == null ? ApiResponse.failure(404, "教师不存在") : ApiResponse.success(teacher);
    }

    /**
     * 新增教师
     */
    @Operation(summary = "教师-创建")
    @PostMapping
    public ApiResponse<Teacher> create(@Parameter(description = "教师实体") @RequestBody Teacher body) {
        body.setId(null);
        boolean ok = teacherService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /**
     * 更新教师
     */
    @Operation(summary = "教师-更新")
    @PutMapping("/{id}")
    public ApiResponse<Teacher> update(@Parameter(description = "教师ID") @PathVariable Long id,
                                       @Parameter(description = "教师实体") @RequestBody Teacher body) {
        body.setId(id);
        boolean ok = teacherService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "教师不存在");
    }

    /**
     * 删除教师
     */
    @Operation(summary = "教师-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "教师ID") @PathVariable Long id) {
        boolean ok = teacherService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "教师不存在");
    }
}
