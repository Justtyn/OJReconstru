package com.oj.onlinejudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.Discussion;
import com.oj.onlinejudge.domain.entity.LoginLog;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.Solution;
import com.oj.onlinejudge.domain.entity.Submission;
import com.oj.onlinejudge.domain.vo.AnalyticsStatusCountVO;
import com.oj.onlinejudge.domain.vo.AnalyticsSummaryVO;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.DiscussionService;
import com.oj.onlinejudge.service.LoginLogService;
import com.oj.onlinejudge.service.ProblemService;
import com.oj.onlinejudge.service.SolutionService;
import com.oj.onlinejudge.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "可视化数据", description = "提供前端数据可视化所需的聚合指标")
public class AnalyticsController {

    private final SubmissionService submissionService;
    private final ProblemService problemService;
    private final SolutionService solutionService;
    private final DiscussionService discussionService;
    private final LoginLogService loginLogService;

    @Operation(summary = "全局概览", description = "返回提交、题目、题解、讨论、登录等核心计数")
    @GetMapping("/summary")
    public ApiResponse<AnalyticsSummaryVO> summary(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current) {
        ensureAdmin(current);
        AnalyticsSummaryVO vo = new AnalyticsSummaryVO();
        vo.setSubmissionTotal(submissionService.count());
        vo.setSubmissionAccepted(submissionService.lambdaQuery().eq(Submission::getOverallStatusId, 3).count());
        vo.setProblemActive(problemService.lambdaQuery().eq(Problem::getIsActive, true).count());
        vo.setProblemInactive(problemService.lambdaQuery().eq(Problem::getIsActive, false).count());
        vo.setSolutionTotal(solutionService.count());
        vo.setSolutionActive(solutionService.lambdaQuery().eq(Solution::getIsActive, true).count());
        vo.setDiscussionTotal(discussionService.count());
        vo.setDiscussionActive(discussionService.lambdaQuery().eq(Discussion::getIsActive, true).count());
        vo.setLoginSuccess(loginLogService.lambdaQuery().eq(LoginLog::getSuccess, true).count());
        vo.setLoginFail(loginLogService.lambdaQuery().eq(LoginLog::getSuccess, false).count());
        return ApiResponse.success(vo);
    }

    @Operation(summary = "提交状态分布", description = "按 overallStatusId 聚合提交数量，供图表展示")
    @GetMapping("/submissions/status")
    public ApiResponse<List<AnalyticsStatusCountVO>> submissionStatus(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current) {
        ensureAdmin(current);
        List<Map<String, Object>> rows = submissionService.listMaps(
                new QueryWrapper<Submission>()
                        .select("overall_status_id as statusId", "COUNT(*) as total")
                        .groupBy("overall_status_id"));
        List<AnalyticsStatusCountVO> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            AnalyticsStatusCountVO item = new AnalyticsStatusCountVO();
            item.setStatusId(row.get("statusId") == null ? null : ((Number) row.get("statusId")).intValue());
            item.setTotal(row.get("total") == null ? 0L : ((Number) row.get("total")).longValue());
            result.add(item);
        }
        return ApiResponse.success(result);
    }

    @Operation(summary = "题目启用状态分布", description = "统计题目启用/禁用数量")
    @GetMapping("/problems/status")
    public ApiResponse<List<AnalyticsStatusCountVO>> problemStatus(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current) {
        ensureAdmin(current);
        List<AnalyticsStatusCountVO> result = new ArrayList<>();
        result.add(new AnalyticsStatusCountVO(null, problemService.lambdaQuery().eq(Problem::getIsActive, true).count(), "active"));
        result.add(new AnalyticsStatusCountVO(null, problemService.lambdaQuery().eq(Problem::getIsActive, false).count(), "inactive"));
        return ApiResponse.success(result);
    }

    @Operation(summary = "登录结果分布", description = "统计登录成功/失败次数，供安全审计图表使用")
    @GetMapping("/logins/status")
    public ApiResponse<List<AnalyticsStatusCountVO>> loginStatus(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current) {
        ensureAdmin(current);
        List<AnalyticsStatusCountVO> result = new ArrayList<>();
        result.add(new AnalyticsStatusCountVO(null, loginLogService.lambdaQuery().eq(LoginLog::getSuccess, true).count(), "success"));
        result.add(new AnalyticsStatusCountVO(null, loginLogService.lambdaQuery().eq(LoginLog::getSuccess, false).count(), "fail"));
        return ApiResponse.success(result);
    }

    private void ensureAdmin(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        if (!"admin".equalsIgnoreCase(current.getRole())) {
            throw ApiException.forbidden("仅管理员可访问可视化数据");
        }
    }
}
