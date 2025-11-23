package com.oj.onlinejudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.HomeworkProblemBatchRequest;
import com.oj.onlinejudge.domain.dto.HomeworkRequest;
import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.domain.entity.Homework;
import com.oj.onlinejudge.domain.entity.HomeworkProblem;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.ClassesService;
import com.oj.onlinejudge.service.HomeworkProblemService;
import com.oj.onlinejudge.service.HomeworkService;
import com.oj.onlinejudge.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.CollectionUtils;
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
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/homeworks")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkProblemService homeworkProblemService;
    private final ProblemService problemService;
    private final ClassesService classesService;

    @Operation(summary = "作业-列表（学生/教师/管理员）")
    @GetMapping
    public ApiResponse<Page<Homework>> list(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "按班级过滤") @RequestParam(required = false) Long classId,
            @Parameter(description = "标题/描述关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "是否仅返回启用作业") @RequestParam(defaultValue = "true") boolean activeOnly) {
        ensureViewer(current);
        LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(Homework::getClassId, classId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Homework::getTitle, keyword)
                    .or()
                    .like(Homework::getDescription, keyword));
        }
        boolean filterInactive = isStudent(current) || (activeOnly && !(isTeacher(current) || isAdmin(current)));
        if (filterInactive) {
            wrapper.eq(Homework::getIsActive, true);
        }
        wrapper.orderByDesc(Homework::getStartTime)
                .orderByDesc(Homework::getCreatedAt);
        Page<Homework> result = homeworkService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    @Operation(summary = "作业-详情")
    @GetMapping("/{id}")
    public ApiResponse<Homework> detail(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureViewer(current);
        Homework homework = homeworkService.getById(id);
        if (homework == null) {
            throw ApiException.notFound("作业不存在");
        }
        ensureActiveForStudent(current, homework);
        return ApiResponse.success(homework);
    }

    @Operation(summary = "作业-题目列表（学生可见）")
    @GetMapping("/{id}/problems")
    public ApiResponse<List<Problem>> listProblems(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureViewer(current);
        Homework homework = homeworkService.getById(id);
        if (homework == null) {
            throw ApiException.notFound("作业不存在");
        }
        ensureActiveForStudent(current, homework);
        List<Long> problemIds = homeworkProblemService.lambdaQuery()
                .eq(HomeworkProblem::getHomeworkId, id)
                .list()
                .stream()
                .map(HomeworkProblem::getProblemId)
                .collect(Collectors.toList());
        if (problemIds.isEmpty()) {
            return ApiResponse.success(Collections.emptyList());
        }
        List<Problem> problems = problemService.listByIds(problemIds);
        return ApiResponse.success(problems);
    }

    @Operation(summary = "作业-创建（教师/管理员）")
    @PostMapping
    public ApiResponse<Homework> create(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Validated(CreateGroup.class) @RequestBody HomeworkRequest request) {
        Classes clazz = ensureCanManageClass(current, request.getClassId());
        validateTimeRange(request.getStartTime(), request.getEndTime());
        Homework body = new Homework();
        BeanUtils.copyProperties(request, body);
        body.setId(null);
        Homework saved = homeworkService.createHomework(body, request.getProblemIds());
        return ApiResponse.success("创建成功", saved);
    }

    @Operation(summary = "作业-更新（教师/管理员）")
    @PutMapping("/{id}")
    public ApiResponse<Homework> update(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id,
            @Validated(UpdateGroup.class) @RequestBody HomeworkRequest request) {
        Homework existing = homeworkService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("作业不存在");
        }
        Long targetClassId = request.getClassId() != null ? request.getClassId() : existing.getClassId();
        ensureCanManageClass(current, targetClassId);
        validateTimeRange(
                request.getStartTime() != null ? request.getStartTime() : existing.getStartTime(),
                request.getEndTime() != null ? request.getEndTime() : existing.getEndTime());
        Homework body = new Homework();
        body.setId(id);
        body.setTitle(request.getTitle() != null ? request.getTitle() : existing.getTitle());
        body.setClassId(targetClassId);
        body.setStartTime(request.getStartTime() != null ? request.getStartTime() : existing.getStartTime());
        body.setEndTime(request.getEndTime() != null ? request.getEndTime() : existing.getEndTime());
        body.setDescription(request.getDescription() != null ? request.getDescription() : existing.getDescription());
        body.setIsActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive());
        Homework updated = homeworkService.updateHomework(body, request.getProblemIds());
        return ApiResponse.success("更新成功", updated);
    }

    @Operation(summary = "作业-删除（教师/管理员）")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        Homework existing = homeworkService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("作业不存在");
        }
        ensureCanManageClass(current, existing.getClassId());
        boolean ok = homeworkService.removeById(id);
        if (!ok) {
            throw ApiException.notFound("作业不存在");
        }
        return ApiResponse.success(null);
    }

    @Operation(summary = "作业题目-批量新增（教师/管理员）")
    @PostMapping("/{id}/problems")
    public ApiResponse<Void> addProblems(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id,
            @Validated @RequestBody HomeworkProblemBatchRequest request) {
        Homework existing = homeworkService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("作业不存在");
        }
        ensureCanManageClass(current, existing.getClassId());
        homeworkService.addProblems(id, request.getProblemIds());
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary = "作业题目-删除（教师/管理员）")
    @DeleteMapping("/{id}/problems/{problemId}")
    public ApiResponse<Void> removeProblem(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id,
            @PathVariable Long problemId) {
        Homework existing = homeworkService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("作业不存在");
        }
        ensureCanManageClass(current, existing.getClassId());
        boolean ok = homeworkService.removeProblem(id, problemId);
        if (!ok) {
            throw ApiException.notFound("题目不在该作业中");
        }
        return ApiResponse.success(null);
    }

    private Classes ensureCanManageClass(AuthenticatedUser current, Long classId) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        Classes clazz = classesService.getById(classId);
        if (clazz == null) {
            throw ApiException.notFound("班级不存在");
        }
        if (isAdmin(current)) {
            return clazz;
        }
        if (!isTeacher(current)) {
            throw ApiException.forbidden("仅教师或管理员可操作作业");
        }
        if (clazz.getCreatorId() != null && !clazz.getCreatorId().equals(current.getUserId())) {
            throw ApiException.forbidden("教师只能管理自己创建的班级作业");
        }
        return clazz;
    }

    private void ensureViewer(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        if (!(isAdmin(current) || isTeacher(current) || isStudent(current))) {
            throw ApiException.forbidden("无权限访问该资源");
        }
    }

    private void ensureActiveForStudent(AuthenticatedUser current, Homework homework) {
        if (isStudent(current) && !Boolean.TRUE.equals(homework.getIsActive())) {
            throw ApiException.notFound("作业不存在");
        }
    }

    private boolean isTeacher(AuthenticatedUser current) {
        return current != null && "teacher".equalsIgnoreCase(current.getRole());
    }

    private boolean isAdmin(AuthenticatedUser current) {
        return current != null && "admin".equalsIgnoreCase(current.getRole());
    }

    private boolean isStudent(AuthenticatedUser current) {
        return current != null && "student".equalsIgnoreCase(current.getRole());
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw ApiException.badRequest("结束时间不能早于开始时间");
        }
    }
}
