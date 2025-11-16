package com.oj.onlinejudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @Operation(summary = "题库-公开列表", description = "支持按名称关键字、难度、挑战标签筛选，只返回启用题目")
    @GetMapping
    public ApiResponse<Page<Problem>> list(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "按题目名称模糊搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "难度过滤：easy/medium/hard") @RequestParam(required = false) String difficulty,
            @Parameter(description = "日常挑战标识") @RequestParam(required = false) String dailyChallenge,
            @Parameter(description = "是否仅返回启用题目") @RequestParam(defaultValue = "true") boolean activeOnly) {
        LambdaQueryWrapper<Problem> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Problem::getName, keyword);
        }
        if (StringUtils.hasText(difficulty)) {
            wrapper.eq(Problem::getDifficulty, difficulty.toLowerCase(Locale.ROOT));
        }
        if (StringUtils.hasText(dailyChallenge)) {
            wrapper.eq(Problem::getDailyChallenge, dailyChallenge);
        }
        if (activeOnly) {
            wrapper.eq(Problem::getIsActive, true);
        }
        wrapper.orderByDesc(Problem::getCreateTime);
        Page<Problem> result = problemService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    @Operation(summary = "题库-公开详情", description = "返回题目完整描述，未启用题目仅管理员/教师可见")
    @GetMapping("/{id}")
    public ApiResponse<Problem> detail(
            @Parameter(description = "题目ID") @PathVariable Long id,
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current) {
        Problem problem = problemService.getById(id);
        if (problem == null) {
            throw ApiException.notFound("题目不存在");
        }
        if (!Boolean.TRUE.equals(problem.getIsActive()) && !isProblemManager(current)) {
            throw ApiException.notFound("题目不存在或未上线");
        }
        return ApiResponse.success(problem);
    }

    private boolean isProblemManager(AuthenticatedUser current) {
        if (current == null || current.getRole() == null) {
            return false;
        }
        String role = current.getRole().toLowerCase();
        return "teacher".equals(role) || "admin".equals(role);
    }
}
