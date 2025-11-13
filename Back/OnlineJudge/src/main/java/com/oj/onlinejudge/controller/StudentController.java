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
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestAttribute;
import com.oj.onlinejudge.security.AuthenticatedUser;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final PasswordService passwordService;

    /** 分页查询学生列表，支持 username/email 模糊搜索 */
    @Operation(summary = "学生-分页列表")
    @GetMapping
    public ApiResponse<Page<Student>> list(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "按用户名模糊搜索") @RequestParam(required = false) String username,
            @Parameter(description = "按邮箱模糊搜索") @RequestParam(required = false) String email,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long page) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
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
    @Operation(summary = "学生-详情")
    @GetMapping("/{id}")
    public ApiResponse<Student> get(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "学生ID") @PathVariable Long id) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        Student student = studentService.getById(id);
        return student == null ? ApiResponse.failure(404, "学生不存在") : ApiResponse.success(student);
    }

    /** 新增学生（如包含明文密码，将进行哈希存储） */
    @Operation(summary = "学生-创建")
    @PostMapping
    public ApiResponse<Student> create(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "学生实体") @RequestBody Student body) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        body.setId(null);
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordService.encode(body.getPassword()));
        }
        boolean ok = studentService.save(body);
        return ok ? ApiResponse.success("创建成功", body) : ApiResponse.failure(500, "创建失败");
    }

    /** 更新学生（未提供 password 时保留原密码） */
    @Operation(summary = "学生-更新")
    @PutMapping("/{id}")
    public ApiResponse<Student> update(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "学生ID") @PathVariable Long id,
            @Parameter(description = "学生实体") @RequestBody Student body) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
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
    @Operation(summary = "学生-删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "学生ID") @PathVariable Long id) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        boolean ok = studentService.removeById(id);
        return ok ? ApiResponse.success(null) : ApiResponse.failure(404, "学生不存在");
    }
}
