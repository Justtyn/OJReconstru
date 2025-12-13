package com.oj.onlinejudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.Discussion;
import com.oj.onlinejudge.domain.entity.LoginLog;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.Solution;
import com.oj.onlinejudge.domain.entity.Submission;
import com.oj.onlinejudge.domain.entity.SubmissionOverallStatus;
import com.oj.onlinejudge.domain.vo.AnalyticsStatusCountVO;
import com.oj.onlinejudge.domain.vo.AnalyticsSummaryVO;
import com.oj.onlinejudge.domain.vo.AnalyticsTimeseriesVO;
import com.oj.onlinejudge.service.DiscussionService;
import com.oj.onlinejudge.service.LoginLogService;
import com.oj.onlinejudge.service.ProblemService;
import com.oj.onlinejudge.service.SolutionService;
import com.oj.onlinejudge.service.SubmissionOverallStatusService;
import com.oj.onlinejudge.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final SubmissionOverallStatusService submissionOverallStatusService;

    @Operation(summary = "全局概览", description = "返回提交、题目、题解、讨论、登录等核心计数")
    @GetMapping("/summary")
    public ApiResponse<AnalyticsSummaryVO> summary(
            @Parameter(description = "当前登录用户") Object current) {
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

    @Operation(summary = "提交状态分布", description = "按 overallStatusId 聚合提交数量，支持时间区间过滤，供图表展示")
    @GetMapping("/submissions/status")
    public ApiResponse<List<AnalyticsStatusCountVO>> submissionStatus(
            @Parameter(description = "当前登录用户") Object current,
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        Map<Integer, SubmissionOverallStatus> statusMap = submissionOverallStatusService.list()
                .stream()
                .collect(Collectors.toMap(SubmissionOverallStatus::getId, s -> s));
        QueryWrapper<Submission> wrapper = new QueryWrapper<Submission>()
                .select("overall_status_id as statusId", "COUNT(*) as total");
        if (startTime != null) {
            wrapper.ge("created_at", startTime);
        }
        if (endTime != null) {
            wrapper.le("created_at", endTime);
        }
        wrapper.groupBy("overall_status_id");
        List<Map<String, Object>> rows = submissionService.listMaps(wrapper);
        List<AnalyticsStatusCountVO> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            AnalyticsStatusCountVO item = new AnalyticsStatusCountVO();
            Integer statusId = row.get("statusId") == null ? null : ((Number) row.get("statusId")).intValue();
            item.setStatusId(statusId);
            item.setTotal(row.get("total") == null ? 0L : ((Number) row.get("total")).longValue());
            SubmissionOverallStatus status = statusMap.get(statusId);
            if (status != null) {
                item.setName(status.getName());
                item.setCode(status.getCode());
            } else if (statusId != null) {
                item.setName("status-" + statusId);
                item.setCode("status-" + statusId);
            } else {
                item.setName("unknown");
                item.setCode("unknown");
            }
            result.add(item);
        }
        fillPercentage(result);
        return ApiResponse.success(result);
    }

    @Operation(summary = "题目启用状态分布", description = "统计题目启用/禁用数量，用于饼图，可按时间过滤")
    @GetMapping("/problems/status")
    public ApiResponse<List<AnalyticsStatusCountVO>> problemStatus(
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        long active = problemService.lambdaQuery()
                .ge(startTime != null, Problem::getCreateTime, startTime)
                .le(endTime != null, Problem::getCreateTime, endTime)
                .eq(Problem::getIsActive, true)
                .count();
        long inactive = problemService.lambdaQuery()
                .ge(startTime != null, Problem::getCreateTime, startTime)
                .le(endTime != null, Problem::getCreateTime, endTime)
                .eq(Problem::getIsActive, false)
                .count();
        List<AnalyticsStatusCountVO> result = new ArrayList<>();
        result.add(new AnalyticsStatusCountVO(null, active, "active", "active"));
        result.add(new AnalyticsStatusCountVO(null, inactive, "inactive", "inactive"));
        fillPercentage(result);
        return ApiResponse.success(result);
    }

    @Operation(summary = "登录结果分布", description = "统计登录成功/失败次数，供安全审计图表使用，可按时间过滤")
    @GetMapping("/logins/status")
    public ApiResponse<List<AnalyticsStatusCountVO>> loginStatus(
            @Parameter(description = "当前登录用户") Object current,
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        List<AnalyticsStatusCountVO> result = new ArrayList<>();
        long success = loginLogService.lambdaQuery()
                .ge(startTime != null, LoginLog::getLoginTime, startTime)
                .le(endTime != null, LoginLog::getLoginTime, endTime)
                .eq(LoginLog::getSuccess, true)
                .count();
        long fail = loginLogService.lambdaQuery()
                .ge(startTime != null, LoginLog::getLoginTime, startTime)
                .le(endTime != null, LoginLog::getLoginTime, endTime)
                .eq(LoginLog::getSuccess, false)
                .count();
        result.add(new AnalyticsStatusCountVO(null, success, "success", "success"));
        result.add(new AnalyticsStatusCountVO(null, fail, "fail", "fail"));
        fillPercentage(result);
        return ApiResponse.success(result);
    }

    @Operation(summary = "提交数量趋势", description = "按日/周/月/年或全部聚合提交量和通过量，支持自定义时间范围绘制折线图")
    @GetMapping("/submissions/timeseries")
    public ApiResponse<List<AnalyticsTimeseriesVO>> submissionTimeseries(
            @Parameter(description = "聚合粒度：day/week/month/year/all，默认 day")
            @RequestParam(value = "granularity", required = false) String granularity,
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        List<Submission> list = submissionService.lambdaQuery()
                .ge(startTime != null, Submission::getCreatedAt, startTime)
                .le(endTime != null, Submission::getCreatedAt, endTime)
                .list();
        Granularity g = Granularity.from(granularity);
        TreeMap<LocalDateTime, AnalyticsTimeseriesVO> buckets = new TreeMap<>();
        for (Submission s : list) {
            LocalDateTime ts = s.getCreatedAt();
            if (ts == null || isOutOfRange(ts, startTime, endTime)) {
                continue;
            }
            LocalDateTime bucket = toBucketStart(ts, g);
            LocalDateTime bucketEnd = toBucketEnd(bucket, g);
            LocalDateTime bucketStartForVo = g == Granularity.ALL ? startTime : bucket;
            LocalDateTime bucketEndForVo = g == Granularity.ALL ? endTime : bucketEnd;
            AnalyticsTimeseriesVO vo = buckets.computeIfAbsent(
                    bucket, k -> new AnalyticsTimeseriesVO(formatLabel(k, g), bucketStartForVo, bucketEndForVo));
            vo.setTotal(vo.getTotal() + 1);
            if (s.getOverallStatusId() != null && s.getOverallStatusId() == 3) {
                vo.setSuccess(vo.getSuccess() + 1);
            } else if (s.getOverallStatusId() != null) {
                vo.setFail(vo.getFail() + 1);
            }
        }
        return ApiResponse.success(new ArrayList<>(buckets.values()));
    }

    @Operation(summary = "登录趋势", description = "按日/周/月/年或全部聚合登录成功/失败次数，支持时间窗口过滤")
    @GetMapping("/logins/timeseries")
    public ApiResponse<List<AnalyticsTimeseriesVO>> loginTimeseries(
            @Parameter(description = "聚合粒度：day/week/month/year/all，默认 day")
            @RequestParam(value = "granularity", required = false) String granularity,
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        List<LoginLog> list = loginLogService.lambdaQuery()
                .ge(startTime != null, LoginLog::getLoginTime, startTime)
                .le(endTime != null, LoginLog::getLoginTime, endTime)
                .list();
        Granularity g = Granularity.from(granularity);
        TreeMap<LocalDateTime, AnalyticsTimeseriesVO> buckets = new TreeMap<>();
        for (LoginLog log : list) {
            LocalDateTime ts = log.getLoginTime();
            if (ts == null || isOutOfRange(ts, startTime, endTime)) {
                continue;
            }
            LocalDateTime bucket = toBucketStart(ts, g);
            LocalDateTime bucketEnd = toBucketEnd(bucket, g);
            LocalDateTime bucketStartForVo = g == Granularity.ALL ? startTime : bucket;
            LocalDateTime bucketEndForVo = g == Granularity.ALL ? endTime : bucketEnd;
            AnalyticsTimeseriesVO vo = buckets.computeIfAbsent(
                    bucket, k -> new AnalyticsTimeseriesVO(formatLabel(k, g), bucketStartForVo, bucketEndForVo));
            vo.setTotal(vo.getTotal() + 1);
            if (Boolean.TRUE.equals(log.getSuccess())) {
                vo.setSuccess(vo.getSuccess() + 1);
            } else {
                vo.setFail(vo.getFail() + 1);
            }
        }
        return ApiResponse.success(new ArrayList<>(buckets.values()));
    }

    @Operation(summary = "题解发布趋势", description = "按日/周/月/年或全部聚合题解数量，支持自定义时间范围")
    @GetMapping("/solutions/timeseries")
    public ApiResponse<List<AnalyticsTimeseriesVO>> solutionTimeseries(
            @Parameter(description = "聚合粒度：day/week/month/year/all，默认 day")
            @RequestParam(value = "granularity", required = false) String granularity,
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        List<Solution> list = solutionService.lambdaQuery()
                .ge(startTime != null, Solution::getCreateTime, startTime)
                .le(endTime != null, Solution::getCreateTime, endTime)
                .list();
        Granularity g = Granularity.from(granularity);
        return ApiResponse.success(toTimeseries(list, Solution::getCreateTime, g, startTime, endTime));
    }

    @Operation(summary = "讨论发布趋势", description = "按日/周/月/年或全部聚合讨论数量，支持自定义时间范围")
    @GetMapping("/discussions/timeseries")
    public ApiResponse<List<AnalyticsTimeseriesVO>> discussionTimeseries(
            @Parameter(description = "聚合粒度：day/week/month/year/all，默认 day")
            @RequestParam(value = "granularity", required = false) String granularity,
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        List<Discussion> list = discussionService.lambdaQuery()
                .ge(startTime != null, Discussion::getCreateTime, startTime)
                .le(endTime != null, Discussion::getCreateTime, endTime)
                .list();
        Granularity g = Granularity.from(granularity);
        return ApiResponse.success(toTimeseries(list, Discussion::getCreateTime, g, startTime, endTime));
    }

    @Operation(summary = "题目发布趋势", description = "按日/周/月/年或全部聚合题目数量，支持自定义时间范围")
    @GetMapping("/problems/timeseries")
    public ApiResponse<List<AnalyticsTimeseriesVO>> problemTimeseries(
            @Parameter(description = "聚合粒度：day/week/month/year/all，默认 day")
            @RequestParam(value = "granularity", required = false) String granularity,
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        List<Problem> list = problemService.lambdaQuery()
                .ge(startTime != null, Problem::getCreateTime, startTime)
                .le(endTime != null, Problem::getCreateTime, endTime)
                .list();
        Granularity g = Granularity.from(granularity);
        return ApiResponse.success(toTimeseries(list, Problem::getCreateTime, g, startTime, endTime));
    }

    @Operation(summary = "题解启用状态分布", description = "用于饼图：启用/禁用题解数量，支持时间过滤")
    @GetMapping("/solutions/status")
    public ApiResponse<List<AnalyticsStatusCountVO>> solutionStatus(
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        long active = solutionService.lambdaQuery()
                .ge(startTime != null, Solution::getCreateTime, startTime)
                .le(endTime != null, Solution::getCreateTime, endTime)
                .eq(Solution::getIsActive, true)
                .count();
        long inactive = solutionService.lambdaQuery()
                .ge(startTime != null, Solution::getCreateTime, startTime)
                .le(endTime != null, Solution::getCreateTime, endTime)
                .eq(Solution::getIsActive, false)
                .count();
        List<AnalyticsStatusCountVO> result = new ArrayList<>();
        result.add(new AnalyticsStatusCountVO(null, active, "active", "active"));
        result.add(new AnalyticsStatusCountVO(null, inactive, "inactive", "inactive"));
        fillPercentage(result);
        return ApiResponse.success(result);
    }

    @Operation(summary = "讨论启用状态分布", description = "用于饼图：启用/禁用讨论数量，支持时间过滤")
    @GetMapping("/discussions/status")
    public ApiResponse<List<AnalyticsStatusCountVO>> discussionStatus(
            @Parameter(description = "起始时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间，ISO-8601，可选")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        long active = discussionService.lambdaQuery()
                .ge(startTime != null, Discussion::getCreateTime, startTime)
                .le(endTime != null, Discussion::getCreateTime, endTime)
                .eq(Discussion::getIsActive, true)
                .count();
        long inactive = discussionService.lambdaQuery()
                .ge(startTime != null, Discussion::getCreateTime, startTime)
                .le(endTime != null, Discussion::getCreateTime, endTime)
                .eq(Discussion::getIsActive, false)
                .count();
        List<AnalyticsStatusCountVO> result = new ArrayList<>();
        result.add(new AnalyticsStatusCountVO(null, active, "active", "active"));
        result.add(new AnalyticsStatusCountVO(null, inactive, "inactive", "inactive"));
        fillPercentage(result);
        return ApiResponse.success(result);
    }

    private <T> List<AnalyticsTimeseriesVO> toTimeseries(
            List<T> list, TimeExtractor<T> extractor, Granularity granularity,
            LocalDateTime startTime, LocalDateTime endTime) {
        TreeMap<LocalDateTime, AnalyticsTimeseriesVO> buckets = new TreeMap<>(Comparator.naturalOrder());
        for (T item : list) {
            LocalDateTime ts = extractor.getTime(item);
            if (ts == null || isOutOfRange(ts, startTime, endTime)) {
                continue;
            }
            LocalDateTime bucket = toBucketStart(ts, granularity);
            LocalDateTime bucketEnd = toBucketEnd(bucket, granularity);
            LocalDateTime bucketStartForVo = granularity == Granularity.ALL ? startTime : bucket;
            LocalDateTime bucketEndForVo = granularity == Granularity.ALL ? endTime : bucketEnd;
            AnalyticsTimeseriesVO vo = buckets.computeIfAbsent(
                    bucket, k -> new AnalyticsTimeseriesVO(formatLabel(k, granularity), bucketStartForVo, bucketEndForVo));
            vo.setTotal(vo.getTotal() + 1);
        }
        return new ArrayList<>(buckets.values());
    }

    private LocalDateTime toBucketStart(LocalDateTime ts, Granularity granularity) {
        switch (granularity) {
            case WEEK:
                LocalDate date = ts.toLocalDate();
                WeekFields wf = WeekFields.ISO;
                LocalDate monday = date.with(wf.dayOfWeek(), DayOfWeek.MONDAY.getValue());
                return monday.atStartOfDay();
            case MONTH:
                return ts.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
            case YEAR:
                return ts.with(TemporalAdjusters.firstDayOfYear()).toLocalDate().atStartOfDay();
            case ALL:
                return LocalDate.MIN.atStartOfDay();
            case DAY:
            default:
                return ts.toLocalDate().atStartOfDay();
        }
    }

    private LocalDateTime toBucketEnd(LocalDateTime bucketStart, Granularity granularity) {
        switch (granularity) {
            case WEEK:
                return bucketStart.plusWeeks(1);
            case MONTH:
                return bucketStart.plusMonths(1);
            case YEAR:
                return bucketStart.plusYears(1);
            case ALL:
                return null;
            case DAY:
            default:
                return bucketStart.plusDays(1);
        }
    }

    private boolean isOutOfRange(LocalDateTime ts, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && ts.isBefore(startTime)) {
            return true;
        }
        if (endTime != null && ts.isAfter(endTime)) {
            return true;
        }
        return false;
    }

    private void fillPercentage(List<AnalyticsStatusCountVO> list) {
        long sum = list.stream()
                .mapToLong(item -> item.getTotal() == null ? 0L : item.getTotal())
                .sum();
        if (sum <= 0) {
            return;
        }
        for (AnalyticsStatusCountVO item : list) {
            long value = item.getTotal() == null ? 0L : item.getTotal();
            item.setPercentage((double) value / sum);
        }
    }

    private String formatLabel(LocalDateTime bucketStart, Granularity granularity) {
        switch (granularity) {
            case WEEK:
                WeekFields wf = WeekFields.ISO;
                int week = bucketStart.get(wf.weekOfWeekBasedYear());
                int year = bucketStart.get(wf.weekBasedYear());
                return year + "-W" + week;
            case MONTH:
                return bucketStart.getYear() + "-" + String.format("%02d", bucketStart.getMonthValue());
            case YEAR:
                return String.valueOf(bucketStart.getYear());
            case ALL:
                return "all";
            case DAY:
            default:
                LocalDate d = bucketStart.toLocalDate();
                return d.toString();
        }
    }

    private enum Granularity {DAY, WEEK, MONTH, YEAR, ALL;
        static Granularity from(String value) {
            if (value == null || value.isBlank()) {
                return DAY;
            }
            switch (value.toLowerCase()) {
                case "week": return WEEK;
                case "month": return MONTH;
                case "year": return YEAR;
                case "all": return ALL;
                case "time":
                case "range": return DAY;
                default: return DAY;
            }
        }
    }

    @FunctionalInterface
    private interface TimeExtractor<T> {
        LocalDateTime getTime(T source);
    }
}
