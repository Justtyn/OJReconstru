package com.oj.onlinejudge.service;

import com.oj.onlinejudge.domain.entity.HomeworkProblem;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.ProblemTestcase;
import com.oj.onlinejudge.domain.entity.Submission;
import com.oj.onlinejudge.domain.entity.SubmissionTestcaseResult;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.judge.Judge0Client;
import com.oj.onlinejudge.judge.Judge0Client.Judge0Request;
import com.oj.onlinejudge.judge.Judge0Client.Judge0Result;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 判题编排服务：
 * - 提交接口只创建记录并进入队列（不等待最终结果）
 * - 后台异步提交到 Judge0（wait=false）并轮询拉取结果
 * - 所有测试点完成后汇总更新 submission，并在状态首次终态化时更新统计
 */
@Service
@RequiredArgsConstructor
public class SubmissionJudgeOrchestrator {

    private static final int JUDGE_STATUS_IN_QUEUE = 1;
    private static final int JUDGE_STATUS_PROCESSING = 2;
    private static final int JUDGE_STATUS_ACCEPTED = 3;
    private static final int JUDGE_STATUS_INTERNAL_ERROR = 13;

    private static final int SUBMISSION_STATUS_PENDING = 1;
    private static final int SUBMISSION_STATUS_RUNNING = 2;
    private static final int SUBMISSION_STATUS_ACCEPTED = 3;
    private static final int SUBMISSION_STATUS_PARTIAL_WRONG = 4;
    private static final int SUBMISSION_STATUS_WRONG = 5;
    private static final int SUBMISSION_STATUS_SYSTEM_ERROR = 6;

    private static final String LOCAL_TOKEN_PREFIX = "LOCAL_";
    private static final String LOCAL_PENDING_PREFIX = "LOCAL_PENDING:";
    private static final String LOCAL_ERROR_PREFIX = "LOCAL_ERROR:";

    private final SubmissionService submissionService;
    private final SubmissionTestcaseResultService submissionTestcaseResultService;
    private final ProblemTestcaseService problemTestcaseService;
    private final Judge0Client judge0Client;
    private final ProblemService problemService;
    private final HomeworkProblemService homeworkProblemService;
    private final StudentService studentService;

    private final ThreadPoolTaskExecutor judgeExecutor;

    @Value("${app.judge.async:true}")
    private boolean asyncEnabled;

    @Value("${app.judge.poll-batch-size:100}")
    private int pollBatchSize;

    public boolean isAsyncEnabled() {
        return asyncEnabled;
    }

    /**
     * 在事务提交后启动判题（避免异步线程读取不到未提交的数据）。
     * test profile 下通常关闭异步，直接同步执行以便测试断言。
     */
    public void startAfterCommit(Long submissionId) {
        if (!asyncEnabled) {
            startNow(submissionId);
            return;
        }
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            startAsync(submissionId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                startAsync(submissionId);
            }
        });
    }

    private void startAsync(Long submissionId) {
        judgeExecutor.execute(() -> startNow(submissionId));
    }

    /**
     * 提交到 Judge0（wait=false），仅获取 token 与初始状态。
     */
    public void startNow(Long submissionId) {
        Submission submission = submissionService.getById(submissionId);
        if (submission == null) {
            return;
        }
        if (submission.getOverallStatusId() == null || submission.getOverallStatusId() != SUBMISSION_STATUS_PENDING) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        boolean started = submissionService.lambdaUpdate()
                .set(Submission::getOverallStatusId, SUBMISSION_STATUS_RUNNING)
                .set(Submission::getUpdatedAt, now)
                .eq(Submission::getId, submissionId)
                .eq(Submission::getOverallStatusId, SUBMISSION_STATUS_PENDING)
                .update();
        if (!started) {
            return;
        }

        List<ProblemTestcase> testcases = problemTestcaseService.lambdaQuery()
                .eq(ProblemTestcase::getProblemId, submission.getProblemId())
                .list();
        if (testcases.isEmpty()) {
            markAllAsSystemError(submissionId, "题目未配置测试用例");
            finalizeIfComplete(submissionId);
            return;
        }
        List<SubmissionTestcaseResult> results = submissionTestcaseResultService.lambdaQuery()
                .eq(SubmissionTestcaseResult::getSubmissionId, submissionId)
                .list();
        Map<Long, SubmissionTestcaseResult> byTestcaseId = new HashMap<>();
        for (SubmissionTestcaseResult r : results) {
            byTestcaseId.put(r.getTestcaseId(), r);
        }

        for (ProblemTestcase testcase : testcases) {
            SubmissionTestcaseResult record = byTestcaseId.get(testcase.getId());
            if (record == null) {
                continue;
            }
            if (record.getJudge0Token() != null && !record.getJudge0Token().startsWith(LOCAL_TOKEN_PREFIX)) {
                continue;
            }
            try {
                Judge0Result r = judge0Client.submit(new Judge0Request(
                        submission.getLanguageId(),
                        submission.getSourceCode(),
                        testcase.getInputData(),
                        testcase.getOutputData()
                ));
                String token = r.getToken();
                if (token == null || token.trim().isEmpty()) {
                    record.setJudge0Token(LOCAL_ERROR_PREFIX + UUID.randomUUID());
                    record.setStatusId(JUDGE_STATUS_INTERNAL_ERROR);
                    record.setMessage("Judge0 返回 token 为空");
                } else {
                    record.setJudge0Token(token);
                    record.setStatusId(sanitizeJudgeStatusId(r.getStatusId(), JUDGE_STATUS_IN_QUEUE));
                    record.setStdout(r.getStdout());
                    record.setStderr(r.getStderr());
                    record.setCompileOutput(r.getCompileOutput());
                    record.setMessage(r.getMessage());
                    if (r.getTime() != null) {
                        record.setTimeUsed(BigDecimal.valueOf(r.getTime()));
                    }
                    record.setMemoryUsed(r.getMemory());
                }
            } catch (Exception ex) {
                record.setJudge0Token(LOCAL_ERROR_PREFIX + UUID.randomUUID());
                record.setStatusId(JUDGE_STATUS_INTERNAL_ERROR);
                record.setMessage("调用判题服务失败: " + ex.getMessage());
            }
            record.setUpdatedAt(LocalDateTime.now());
            submissionTestcaseResultService.updateById(record);
        }
        finalizeIfComplete(submissionId);
    }

    @Scheduled(fixedDelayString = "${app.judge.poll-delay-ms:1000}")
    public void pollPendingResults() {
        if (!asyncEnabled) {
            return;
        }
        List<SubmissionTestcaseResult> pending = submissionTestcaseResultService.lambdaQuery()
                .in(SubmissionTestcaseResult::getStatusId, JUDGE_STATUS_IN_QUEUE, JUDGE_STATUS_PROCESSING)
                .notLikeRight(SubmissionTestcaseResult::getJudge0Token, LOCAL_TOKEN_PREFIX)
                .last("limit " + pollBatchSize)
                .list();
        if (pending.isEmpty()) {
            return;
        }
        Set<Long> touchedSubmissionIds = new HashSet<>();
        for (SubmissionTestcaseResult record : pending) {
            String token = record.getJudge0Token();
            if (token == null || token.trim().isEmpty()) {
                record.setJudge0Token(LOCAL_ERROR_PREFIX + UUID.randomUUID());
                record.setStatusId(JUDGE_STATUS_INTERNAL_ERROR);
                record.setMessage("Judge0 Token 为空");
                record.setUpdatedAt(LocalDateTime.now());
                submissionTestcaseResultService.updateById(record);
                touchedSubmissionIds.add(record.getSubmissionId());
                continue;
            }
            try {
                Judge0Result r = judge0Client.get(token);
                record.setStatusId(sanitizeJudgeStatusId(r.getStatusId(), record.getStatusId()));
                record.setStdout(r.getStdout());
                record.setStderr(r.getStderr());
                record.setCompileOutput(r.getCompileOutput());
                record.setMessage(r.getMessage());
                if (r.getTime() != null) {
                    record.setTimeUsed(BigDecimal.valueOf(r.getTime()));
                }
                record.setMemoryUsed(r.getMemory());
            } catch (Exception ex) {
                record.setStatusId(JUDGE_STATUS_INTERNAL_ERROR);
                record.setMessage("拉取判题结果失败: " + ex.getMessage());
            }
            record.setUpdatedAt(LocalDateTime.now());
            submissionTestcaseResultService.updateById(record);
            touchedSubmissionIds.add(record.getSubmissionId());
        }
        for (Long submissionId : touchedSubmissionIds) {
            finalizeIfComplete(submissionId);
        }
    }

    private void finalizeIfComplete(Long submissionId) {
        Submission submission = submissionService.getById(submissionId);
        if (submission == null) {
            return;
        }
        if (submission.getOverallStatusId() != null
                && submission.getOverallStatusId() != SUBMISSION_STATUS_PENDING
                && submission.getOverallStatusId() != SUBMISSION_STATUS_RUNNING) {
            return;
        }
        List<SubmissionTestcaseResult> results = submissionTestcaseResultService.lambdaQuery()
                .eq(SubmissionTestcaseResult::getSubmissionId, submissionId)
                .list();
        if (results.isEmpty()) {
            return;
        }
        for (SubmissionTestcaseResult r : results) {
            if (r.getJudge0Token() != null && r.getJudge0Token().startsWith(LOCAL_PENDING_PREFIX)) {
                return;
            }
            if (r.getStatusId() != null
                    && (r.getStatusId() == JUDGE_STATUS_IN_QUEUE || r.getStatusId() == JUDGE_STATUS_PROCESSING)) {
                return;
            }
        }

        int total = results.size();
        int passed = 0;
        boolean systemError = false;
        for (SubmissionTestcaseResult r : results) {
            if (r.getStatusId() != null && r.getStatusId() == JUDGE_STATUS_ACCEPTED) {
                passed++;
            }
            if (r.getStatusId() == null || r.getStatusId() == JUDGE_STATUS_INTERNAL_ERROR) {
                systemError = true;
            }
        }

        int overallStatus = resolveOverallStatus(systemError, passed, total);
        int score = calculateScore(passed, total);
        LocalDateTime now = LocalDateTime.now();

        boolean updated = submissionService.lambdaUpdate()
                .set(Submission::getOverallStatusId, overallStatus)
                .set(Submission::getPassedCaseCount, passed)
                .set(Submission::getTotalCaseCount, total)
                .set(Submission::getScore, score)
                .set(Submission::getUpdatedAt, now)
                .eq(Submission::getId, submissionId)
                .in(Submission::getOverallStatusId, SUBMISSION_STATUS_PENDING, SUBMISSION_STATUS_RUNNING)
                .update();
        if (!updated) {
            return;
        }

        if (overallStatus == SUBMISSION_STATUS_ACCEPTED) {
            applyAcceptedStatistics(submission);
        }
    }

    public static String newLocalPendingToken() {
        return LOCAL_PENDING_PREFIX + UUID.randomUUID();
    }

    private void markAllAsSystemError(Long submissionId, String message) {
        List<SubmissionTestcaseResult> results = submissionTestcaseResultService.lambdaQuery()
                .eq(SubmissionTestcaseResult::getSubmissionId, submissionId)
                .list();
        for (SubmissionTestcaseResult record : results) {
            record.setJudge0Token(LOCAL_ERROR_PREFIX + UUID.randomUUID());
            record.setStatusId(JUDGE_STATUS_INTERNAL_ERROR);
            record.setMessage(message);
            record.setUpdatedAt(LocalDateTime.now());
            submissionTestcaseResultService.updateById(record);
        }
    }

    private int resolveOverallStatus(boolean systemError, int passed, int total) {
        if (systemError) {
            return SUBMISSION_STATUS_SYSTEM_ERROR;
        }
        if (passed == total) {
            return SUBMISSION_STATUS_ACCEPTED;
        }
        if (passed > 0) {
            return SUBMISSION_STATUS_PARTIAL_WRONG;
        }
        return SUBMISSION_STATUS_WRONG;
    }

    private int calculateScore(int passed, int total) {
        if (total == 0) {
            return 0;
        }
        return BigDecimal.valueOf(passed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private int sanitizeJudgeStatusId(Integer raw, int fallback) {
        if (raw == null) {
            return fallback;
        }
        if (raw == JUDGE_STATUS_IN_QUEUE || raw == JUDGE_STATUS_PROCESSING || raw == JUDGE_STATUS_ACCEPTED
                || raw == 4 || raw == 5 || raw == 6 || raw == 7 || raw == 8 || raw == JUDGE_STATUS_INTERNAL_ERROR) {
            return raw;
        }
        return JUDGE_STATUS_INTERNAL_ERROR;
    }

    private void applyAcceptedStatistics(Submission submission) {
        Problem problem = problemService.getById(submission.getProblemId());
        if (problem != null) {
            problem.setAcCount(safeCount(problem.getAcCount()) + 1);
            problemService.updateById(problem);
        }
        if (submission.getHomeworkId() != null) {
            HomeworkProblem hp = homeworkProblemService.lambdaQuery()
                    .eq(HomeworkProblem::getHomeworkId, submission.getHomeworkId())
                    .eq(HomeworkProblem::getProblemId, submission.getProblemId())
                    .one();
            if (hp != null) {
                int acCount = safeCount(hp.getAcCount()) + 1;
                homeworkProblemService.lambdaUpdate()
                        .eq(HomeworkProblem::getHomeworkId, submission.getHomeworkId())
                        .eq(HomeworkProblem::getProblemId, submission.getProblemId())
                        .set(HomeworkProblem::getAcCount, acCount)
                        .update();
            }
        }
        Student student = studentService.getById(submission.getStudentId());
        if (student != null) {
            student.setAc(safeCount(student.getAc()) + 1);
            student.setScore(safeCount(student.getScore()) + 5);
            studentService.updateById(student);
        }
    }

    private int safeCount(Integer count) {
        return count == null ? 0 : count;
    }
}
