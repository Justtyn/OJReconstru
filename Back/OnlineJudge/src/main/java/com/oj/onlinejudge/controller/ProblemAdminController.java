package com.oj.onlinejudge.controller;

import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.ProblemTestcaseRequest;
import com.oj.onlinejudge.domain.dto.ProblemUpsertRequest;
import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.ProblemTestcase;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.ProblemService;
import com.oj.onlinejudge.service.ProblemTestcaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.Locale;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ProblemAdminController {

    private final ProblemService problemService;
    private final ProblemTestcaseService problemTestcaseService;

    @Operation(summary = "题库-新增题目")
    @PostMapping("/problems")
    public ApiResponse<Problem> createProblem(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Validated(CreateGroup.class) @RequestBody ProblemUpsertRequest request) {
        ensureProblemManager(current);
        Problem problem = new Problem();
        BeanUtils.copyProperties(request, problem);
        problem.setId(null);
        normalizeProblemDefaults(problem, true);
        boolean ok = problemService.save(problem);
        if (!ok) {
            throw ApiException.internal("创建题目失败");
        }
        return ApiResponse.success("创建成功", problem);
    }

    @Operation(summary = "题库-更新题目")
    @PutMapping("/problems/{id}")
    public ApiResponse<Problem> updateProblem(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "题目ID") @PathVariable Long id,
            @Validated(UpdateGroup.class) @RequestBody ProblemUpsertRequest request) {
        ensureProblemManager(current);
        ensureProblemExists(id);
        Problem problem = new Problem();
        BeanUtils.copyProperties(request, problem);
        problem.setId(id);
        normalizeProblemDefaults(problem, false);
        boolean ok = problemService.updateById(problem);
        if (!ok) {
            throw ApiException.internal("更新题目失败");
        }
        return ApiResponse.success("更新成功", problem);
    }

    @Operation(summary = "题库-删除题目", description = "删除题目同时移除关联测试用例")
    @DeleteMapping("/problems/{id}")
    public ApiResponse<Void> deleteProblem(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "题目ID") @PathVariable Long id) {
        ensureProblemManager(current);
        if (!problemService.removeProblemWithTestcases(id)) {
            throw ApiException.notFound("题目不存在");
        }
        return ApiResponse.success(null);
    }

    @Operation(summary = "题库-测试用例列表")
    @GetMapping("/problems/{id}/testcases")
    public ApiResponse<List<ProblemTestcase>> listTestcases(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "题目ID") @PathVariable Long id) {
        ensureProblemManager(current);
        ensureProblemExists(id);
        List<ProblemTestcase> list = problemTestcaseService.lambdaQuery()
                .eq(ProblemTestcase::getProblemId, id)
                .list();
        return ApiResponse.success(list);
    }

    @Operation(summary = "题库-新增测试用例")
    @PostMapping("/problems/{id}/testcases")
    public ApiResponse<ProblemTestcase> createTestcase(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "题目ID") @PathVariable Long id,
            @Validated(CreateGroup.class) @RequestBody ProblemTestcaseRequest request) {
        ensureProblemManager(current);
        ensureProblemExists(id);
        ProblemTestcase testcase = new ProblemTestcase();
        testcase.setProblemId(id);
        BeanUtils.copyProperties(request, testcase);
        boolean ok = problemTestcaseService.save(testcase);
        if (!ok) {
            throw ApiException.internal("创建测试用例失败");
        }
        return ApiResponse.success("创建成功", testcase);
    }

    @Operation(summary = "题库-更新测试用例")
    @PutMapping("/problem-testcases/{testcaseId}")
    public ApiResponse<ProblemTestcase> updateTestcase(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "测试用例ID") @PathVariable Long testcaseId,
            @Validated(UpdateGroup.class) @RequestBody ProblemTestcaseRequest request) {
        ensureProblemManager(current);
        ProblemTestcase existing = problemTestcaseService.getById(testcaseId);
        if (existing == null) {
            throw ApiException.notFound("测试用例不存在");
        }
        ProblemTestcase testcase = new ProblemTestcase();
        BeanUtils.copyProperties(request, testcase);
        testcase.setId(testcaseId);
        testcase.setProblemId(existing.getProblemId());
        boolean ok = problemTestcaseService.updateById(testcase);
        if (!ok) {
            throw ApiException.internal("更新测试用例失败");
        }
        return ApiResponse.success("更新成功", testcase);
    }

    @Operation(summary = "题库-删除测试用例")
    @DeleteMapping("/problem-testcases/{testcaseId}")
    public ApiResponse<Void> deleteTestcase(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "测试用例ID") @PathVariable Long testcaseId) {
        ensureProblemManager(current);
        boolean ok = problemTestcaseService.removeById(testcaseId);
        if (!ok) {
            throw ApiException.notFound("测试用例不存在");
        }
        return ApiResponse.success(null);
    }

    private void ensureProblemManager(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        if (!isProblemManager(current)) {
            throw ApiException.forbidden("仅教师或管理员可执行该操作");
        }
    }

    private boolean isProblemManager(AuthenticatedUser current) {
        if (current == null || current.getRole() == null) {
            return false;
        }
        String role = current.getRole().toLowerCase(Locale.ROOT);
        return "teacher".equals(role) || "admin".equals(role);
    }

    private void ensureProblemExists(Long problemId) {
        if (problemService.getById(problemId) == null) {
            throw ApiException.notFound("题目不存在");
        }
    }

    private void normalizeProblemDefaults(Problem problem, boolean creating) {
        if (creating && problem.getIsActive() == null) {
            problem.setIsActive(true);
        }
        if (creating && problem.getAcCount() == null) {
            problem.setAcCount(0);
        }
        if (creating && problem.getSubmitCount() == null) {
            problem.setSubmitCount(0);
        }
        if (!org.springframework.util.StringUtils.hasText(problem.getDailyChallenge()) && creating) {
            problem.setDailyChallenge("0");
        }
    }
}
