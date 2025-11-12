package com.oj.onlinejudge.controller;

// 学生模块 REST 控制器：提供学生的增删改查接口
// 注意：创建/更新时如果传入 password 字段，则会进行密码哈希；不传则更新时保持原密码不变

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.security.PasswordService;
import com.oj.onlinejudge.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "学生管理", description = "学生的增删改查接口")
public class StudentController {

    private final StudentService studentService;
    private final PasswordService passwordService;

    /** 分页查询学生列表，支持 username/email 模糊搜索 */
    @GetMapping
    @Operation(summary = "分页列表", description = "分页获取学生列表，支持用户名/邮箱模糊搜索")
    @Parameters({
        @Parameter(name = "page", description = "页码(从1开始)", required = false),
        @Parameter(name = "size", description = "每页数量", required = false),
        @Parameter(name = "username", description = "按用户名模糊搜索", required = false),
        @Parameter(name = "email", description = "按邮箱模糊搜索", required = false)
    })
    public ApiResponse<Page<Student>> list(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String email) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(Student::getUsername, username);
        }
        if (StringUtils.hasText(email)) {
            wrapper.like(Student::getEmail, email);
        }
        Page<Student> p = studentService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(p);
    }

    /** 根据ID查询学生详情 */
    @GetMapping("/{id}")
    @Operation(summary = "学生详情", description = "根据ID获取学生信息")
    public ApiResponse<Student> get(@Parameter(description = "学生ID") @PathVariable Long id) {
        Student student = studentService.getById(id);
        return student == null ? ApiResponse.failure(404, "学生不存在") : ApiResponse.success(student);
    }

    /** 新增学生（如包含明文密码，将进行哈希存储） */
    @PostMapping
    @Operation(summary = "创建学生", description = "新增一个学生账号，若包含密码将进行哈希")
    public ApiResponse<Student> create(@RequestBody Student body) {
        body.setId(null);
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordService.encode(body.getPassword()));
        }
        boolean ok = studentService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /** 更新学生（未提供 password 时保留原密码） */
    @PutMapping("/{id}")
    @Operation(summary = "更新学生", description = "根据ID更新学生信息，未提供密码时保留原密码")
    public ApiResponse<Student> update(@Parameter(description = "学生ID") @PathVariable Long id,
                                       @RequestBody Student body) {
        body.setId(id);
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordService.encode(body.getPassword()));
        } else {
            // 不修改密码: 读取旧值保持
            Student old = studentService.getById(id);
            if (old == null) {
                return ApiResponse.failure(404, "学生不存在");
            }
            body.setPassword(old.getPassword());
        }
        boolean ok = studentService.updateById(body);
        return ok ? ApiResponse.success("更新成功", body) : ApiResponse.failure(404, "学生不存在");
    }

    /** 删除学生 */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生", description = "根据ID删除学生")
    public ApiResponse<Void> delete(@Parameter(description = "学生ID") @PathVariable Long id) {
        boolean ok = studentService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "学生不存在");
    }
}
