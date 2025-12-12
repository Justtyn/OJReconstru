package com.oj.onlinejudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.SolutionRequest;
import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.Solution;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.ProblemService;
import com.oj.onlinejudge.service.SolutionService;
import com.oj.onlinejudge.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;
    private final ProblemService problemService;
    private final StudentService studentService;

    @Operation(summary = "题解-列表", description = "学生/教师/管理员均可查看，可按题目或作者筛选")
    @GetMapping("/solutions")
    public ApiResponse<Page<Solution>> list(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "题目ID过滤") @RequestParam(required = false) Long problemId,
            @Parameter(description = "作者学生ID过滤") @RequestParam(required = false) Long authorId,
            @Parameter(description = "是否包含未启用题解，仅管理员可用") @RequestParam(defaultValue = "false") boolean includeInactive) {
        ensureViewRole(current);
        boolean includeInactiveAll = isAdmin(current) || (includeInactive && isAdmin(current));
        Page<Solution> result = querySolutions(problemId, authorId, includeInactiveAll, page, size);
        return ApiResponse.success(result);
    }

    @Operation(summary = "题解-按题目列表")
    @GetMapping("/problems/{problemId}/solutions")
    public ApiResponse<Page<Solution>> listByProblem(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        ensureViewRole(current);
        ensureProblemExists(problemId);
        boolean includeInactiveAll = isAdmin(current);
        Page<Solution> result = querySolutions(problemId, null, includeInactiveAll, page, size);
        return ApiResponse.success(result);
    }

    @Operation(summary = "题解-详情")
    @GetMapping("/solutions/{id}")
    public ApiResponse<Solution> detail(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureViewRole(current);
        Solution solution = solutionService.getById(id);
        if (solution == null) {
            throw ApiException.notFound("题解不存在");
        }
        if (!Boolean.TRUE.equals(solution.getIsActive()) && !isAdmin(current) && !isOwner(current, solution)) {
            throw ApiException.notFound("题解不存在");
        }
        return ApiResponse.success(solution);
    }

    @Operation(summary = "题解-创建", description = "学生或管理员可发布，管理员需指定 authorId 为学生ID，路径参数指定题目ID；发布成功为作者积分+3")
    @PostMapping("/problems/{problemId}/solutions")
    public ApiResponse<Solution> create(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long problemId,
            @Validated(CreateGroup.class) @RequestBody SolutionRequest request) {
        ensureStudentOrAdmin(current);
        ensureProblemExists(problemId);
        Solution body = new Solution();
        BeanUtils.copyProperties(request, body);
        body.setId(null);
        body.setProblemId(problemId);
        body.setUserId(resolveAuthorId(current, request.getAuthorId()));
        body.setCreateTime(LocalDateTime.now());
        if (body.getIsActive() == null) {
            body.setIsActive(true);
        }
        boolean ok = solutionService.save(body);
        if (!ok) {
            throw ApiException.internal("创建题解失败");
        }
        addScoreForStudent(body.getUserId(), 3);
        return ApiResponse.success("创建成功", body);
    }

    @Operation(summary = "题解-更新", description = "作者或管理员可修改")
    @PutMapping("/solutions/{id}")
    public ApiResponse<Solution> update(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id,
            @Validated(UpdateGroup.class) @RequestBody SolutionRequest request) {
        ensureViewRole(current);
        Solution existing = solutionService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("题解不存在");
        }
        ensureOwnerOrAdmin(current, existing);
        if (request.getProblemId() != null && !request.getProblemId().equals(existing.getProblemId())) {
            ensureProblemExists(request.getProblemId());
            existing.setProblemId(request.getProblemId());
        }
        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            existing.setContent(request.getContent());
        }
        if (request.getLanguage() != null) {
            existing.setLanguage(request.getLanguage());
        }
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }
        boolean ok = solutionService.updateById(existing);
        if (!ok) {
            throw ApiException.internal("更新题解失败");
        }
        return ApiResponse.success("更新成功", existing);
    }

    @Operation(summary = "题解-删除", description = "作者或管理员可删除")
    @DeleteMapping("/solutions/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureViewRole(current);
        Solution existing = solutionService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("题解不存在");
        }
        ensureOwnerOrAdmin(current, existing);
        solutionService.removeById(id);
        return ApiResponse.success(null);
    }

    private Page<Solution> querySolutions(Long problemId, Long authorId, boolean includeInactive, long page, long size) {
        LambdaQueryWrapper<Solution> wrapper = new LambdaQueryWrapper<>();
        if (problemId != null) {
            wrapper.eq(Solution::getProblemId, problemId);
        }
        if (authorId != null) {
            wrapper.eq(Solution::getUserId, authorId);
        }
        if (!includeInactive) {
            wrapper.eq(Solution::getIsActive, true);
        }
        wrapper.orderByDesc(Solution::getCreateTime)
                .orderByDesc(Solution::getUpdatedAt);
        return solutionService.page(new Page<>(page, size), wrapper);
    }

    private void ensureProblemExists(Long problemId) {
        Problem problem = problemService.getById(problemId);
        if (problem == null) {
            throw ApiException.notFound("题目不存在");
        }
    }

    private void ensureStudentOrAdmin(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        String role = current.getRole() == null ? "" : current.getRole().toLowerCase();
        if (!"student".equals(role) && !"admin".equals(role)) {
            throw ApiException.forbidden("仅学生或管理员可执行此操作");
        }
    }

    private Long resolveAuthorId(AuthenticatedUser current, Long requestedAuthorId) {
        boolean admin = isAdmin(current);
        if (admin) {
            if (requestedAuthorId == null) {
                throw ApiException.badRequest("管理员发布题解需指定 authorId（学生ID）");
            }
            ensureStudentExists(requestedAuthorId);
            return requestedAuthorId;
        }
        ensureStudentExists(current.getUserId());
        return current.getUserId();
    }

    private void ensureViewRole(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        String role = current.getRole() == null ? "" : current.getRole().toLowerCase();
        if (!("student".equals(role) || "teacher".equals(role) || "admin".equals(role))) {
            throw ApiException.forbidden("无权限访问题解");
        }
    }

    private boolean isAdmin(AuthenticatedUser current) {
        return current != null && "admin".equalsIgnoreCase(current.getRole());
    }

    private boolean isOwner(AuthenticatedUser current, Solution solution) {
        return current != null && "student".equalsIgnoreCase(current.getRole())
                && solution.getUserId() != null && solution.getUserId().equals(current.getUserId());
    }

    private void ensureOwnerOrAdmin(AuthenticatedUser current, Solution solution) {
        if (!(isAdmin(current) || isOwner(current, solution))) {
            throw ApiException.forbidden("无权操作该题解");
        }
    }

    private void ensureStudentExists(Long studentId) {
        if (studentId == null || studentService.getById(studentId) == null) {
            throw ApiException.badRequest("指定的学生不存在");
        }
    }

    private void addScoreForStudent(Long studentId, int delta) {
        if (studentId == null || delta <= 0) {
            return;
        }
        Student student = studentService.getById(studentId);
        if (student == null) {
            return;
        }
        student.setScore((student.getScore() == null ? 0 : student.getScore()) + delta);
        studentService.updateById(student);
    }
}
