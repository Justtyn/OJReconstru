package com.oj.onlinejudge.controller;

// 班级成员模块 REST 控制器：维护班级与成员（学生/教师）的关系
// 支持按班级ID过滤、分页查询、详情、新增、更新、删除

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.ClassesMember;
import com.oj.onlinejudge.service.ClassesMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes-members")
@RequiredArgsConstructor
@Tag(name = "班级成员管理", description = "班级-成员关系的查询与维护")
public class ClassesMemberController {

    private final ClassesMemberService classesMemberService;

    /**
     * 分页查询班级成员（可选按classId过滤）
     */
    @GetMapping
    @Operation(summary = "分页列表", description = "分页获取班级成员列表，可选按classId过滤")
    @Parameters({
        @Parameter(name = "page", description = "页码(从1开始)", required = false),
        @Parameter(name = "size", description = "每页数量", required = false),
        @Parameter(name = "classId", description = "按班级ID过滤", required = false)
    })
    public ApiResponse<Page<ClassesMember>> list(@RequestParam(defaultValue = "1") long page,
                                                 @RequestParam(defaultValue = "10") long size,
                                                 @RequestParam(required = false) Long classId) {
        LambdaQueryWrapper<ClassesMember> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(ClassesMember::getClassId, classId);
        }
        Page<ClassesMember> p = classesMemberService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(p);
    }

    /** 查询详情 */
    @GetMapping("/{id}")
    @Operation(summary = "成员记录详情", description = "根据ID获取班级成员记录")
    public ApiResponse<ClassesMember> get(@Parameter(description = "记录ID") @PathVariable Long id) {
        ClassesMember cm = classesMemberService.getById(id);
        return cm == null ? ApiResponse.failure(404, "记录不存在") : ApiResponse.success(cm);
    }

    /** 新增记录 */
    @PostMapping
    @Operation(summary = "新增成员记录", description = "向班级中新增成员关系记录")
    public ApiResponse<ClassesMember> create(@RequestBody ClassesMember body) {
        body.setId(null);
        boolean ok = classesMemberService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /** 更新记录 */
    @PutMapping("/{id}")
    @Operation(summary = "更新成员记录", description = "根据ID更新成员关系记录")
    public ApiResponse<ClassesMember> update(@Parameter(description = "记录ID") @PathVariable Long id,
                                             @RequestBody ClassesMember body) {
        body.setId(id);
        boolean ok = classesMemberService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "记录不存在");
    }

    /** 删除记录 */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除成员记录", description = "根据ID删除成员关系记录")
    public ApiResponse<Void> delete(@Parameter(description = "记录ID") @PathVariable Long id) {
        boolean ok = classesMemberService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "记录不存在");
    }
}
