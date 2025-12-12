package com.oj.onlinejudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.SubmissionCreateRequest;
import com.oj.onlinejudge.domain.entity.Submission;
import com.oj.onlinejudge.domain.entity.SubmissionTestcaseResult;
import com.oj.onlinejudge.domain.entity.SubmissionOverallStatus;
import com.oj.onlinejudge.domain.vo.SubmissionDetailVO;
import com.oj.onlinejudge.domain.vo.SubmissionTestcaseResultVO;
import com.oj.onlinejudge.domain.vo.SubmissionVO;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.SubmissionApplicationService;
import com.oj.onlinejudge.service.SubmissionOverallStatusService;
import com.oj.onlinejudge.service.SubmissionService;
import com.oj.onlinejudge.service.SubmissionTestcaseResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionApplicationService submissionApplicationService;
    private final SubmissionService submissionService;
    private final SubmissionTestcaseResultService submissionTestcaseResultService;
    private final SubmissionOverallStatusService submissionOverallStatusService;

    @Operation(summary = "提交代码（学生）", description = "判题通过（ACCEPTED）后为该学生积分+5，其他状态不加分")
    @PostMapping
    public ApiResponse<SubmissionDetailVO> create(
            @Parameter(description = "当前学生") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Valid @RequestBody SubmissionCreateRequest request) {
        SubmissionDetailVO detail = submissionApplicationService.submit(request, current);
        return ApiResponse.success("提交成功", detail);
    }

    @Operation(summary = "提交记录-分页列表")
    @GetMapping
    public ApiResponse<Page<SubmissionVO>> list(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long problemId,
            @RequestParam(required = false) Long homeworkId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer statusId) {
        ensureLoggedIn(current);
        LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<>();
        if (problemId != null) {
            wrapper.eq(Submission::getProblemId, problemId);
        }
        if (homeworkId != null) {
            wrapper.eq(Submission::getHomeworkId, homeworkId);
        }
        if (statusId != null) {
            wrapper.eq(Submission::getOverallStatusId, statusId);
        }
        if (isStudent(current)) {
            wrapper.eq(Submission::getStudentId, current.getUserId());
        } else if (studentId != null) {
            wrapper.eq(Submission::getStudentId, studentId);
        }
        wrapper.orderByDesc(Submission::getCreatedAt);
        Page<Submission> raw = submissionService.page(new Page<>(page, size), wrapper);
        Map<Integer, SubmissionOverallStatus> statusMap = submissionOverallStatusService.list()
                .stream().collect(Collectors.toMap(SubmissionOverallStatus::getId, s -> s));
        Page<SubmissionVO> converted = new Page<>(raw.getCurrent(), raw.getSize(), raw.getTotal());
        converted.setRecords(raw.getRecords().stream()
                .map(item -> toVO(item, statusMap))
                .collect(Collectors.toList()));
        return ApiResponse.success(converted);
    }

    @Operation(summary = "提交详情")
    @GetMapping("/{id}")
    public ApiResponse<SubmissionDetailVO> detail(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureLoggedIn(current);
        Submission submission = submissionService.getById(id);
        if (submission == null) {
            throw ApiException.notFound("提交不存在");
        }
        ensureCanView(current, submission);
        List<SubmissionTestcaseResult> results = submissionTestcaseResultService.lambdaQuery()
                .eq(SubmissionTestcaseResult::getSubmissionId, submission.getId())
                .list();
        SubmissionDetailVO detail = submissionApplicationService.buildDetailVO(submission, results);
        if (!(isAdmin(current) || isOwner(current, submission))) {
            detail.setSourceCode(null);
        }
        return ApiResponse.success(detail);
    }

    private SubmissionVO toVO(Submission submission, Map<Integer, SubmissionOverallStatus> statusMap) {
        SubmissionVO vo = new SubmissionVO();
        vo.setId(submission.getId());
        vo.setProblemId(submission.getProblemId());
        vo.setHomeworkId(submission.getHomeworkId());
        vo.setLanguageId(submission.getLanguageId());
        vo.setOverallStatusId(submission.getOverallStatusId());
        vo.setPassedCaseCount(submission.getPassedCaseCount());
        vo.setTotalCaseCount(submission.getTotalCaseCount());
        vo.setScore(submission.getScore());
        vo.setCreatedAt(submission.getCreatedAt());
        vo.setUpdatedAt(submission.getUpdatedAt());
        vo.setStudentId(submission.getStudentId());
        SubmissionOverallStatus status = statusMap.get(submission.getOverallStatusId());
        if (status != null) {
            vo.setOverallStatusCode(status.getCode());
            vo.setOverallStatusName(status.getName());
        }
        return vo;
    }

    private void ensureLoggedIn(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
    }

    private void ensureCanView(AuthenticatedUser current, Submission submission) {
        if (isAdmin(current) || isTeacher(current)) {
            return;
        }
        if (isOwner(current, submission)) {
            return;
        }
        throw ApiException.forbidden("无权查看该提交");
    }

    private boolean isAdmin(AuthenticatedUser current) {
        return current != null && "admin".equalsIgnoreCase(current.getRole());
    }

    private boolean isTeacher(AuthenticatedUser current) {
        return current != null && "teacher".equalsIgnoreCase(current.getRole());
    }

    private boolean isStudent(AuthenticatedUser current) {
        return current != null && "student".equalsIgnoreCase(current.getRole());
    }

    private boolean isOwner(AuthenticatedUser current, Submission submission) {
        return current != null && isStudent(current)
                && submission.getStudentId() != null
                && submission.getStudentId().equals(current.getUserId());
    }
}
