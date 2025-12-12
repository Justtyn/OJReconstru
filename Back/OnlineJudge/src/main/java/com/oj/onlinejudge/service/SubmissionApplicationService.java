package com.oj.onlinejudge.service;

import com.oj.onlinejudge.domain.dto.SubmissionCreateRequest;
import com.oj.onlinejudge.domain.entity.Homework;
import com.oj.onlinejudge.domain.entity.HomeworkProblem;
import com.oj.onlinejudge.domain.entity.JudgeStatus;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.ProblemTestcase;
import com.oj.onlinejudge.domain.entity.Submission;
import com.oj.onlinejudge.domain.entity.SubmissionOverallStatus;
import com.oj.onlinejudge.domain.entity.SubmissionTestcaseResult;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.domain.vo.SubmissionDetailVO;
import com.oj.onlinejudge.domain.vo.SubmissionTestcaseResultVO;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.judge.Judge0Client;
import com.oj.onlinejudge.judge.Judge0Client.Judge0Request;
import com.oj.onlinejudge.judge.Judge0Client.Judge0Result;
import com.oj.onlinejudge.security.AuthenticatedUser;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmissionApplicationService {

    private final SubmissionService submissionService;
    private final SubmissionTestcaseResultService submissionTestcaseResultService;
    private final SubmissionOverallStatusService submissionOverallStatusService;
    private final ProblemService problemService;
    private final ProblemTestcaseService problemTestcaseService;
    private final HomeworkService homeworkService;
    private final HomeworkProblemService homeworkProblemService;
    private final Judge0Client judge0Client;
    private final JudgeStatusService judgeStatusService;
    private final StudentService studentService;

    @Transactional(rollbackFor = Exception.class)
    public SubmissionDetailVO submit(SubmissionCreateRequest request, AuthenticatedUser current) {
        Long targetStudentId = resolveStudentId(request, current);
        Problem problem = requireProblem(request.getProblemId());
        Homework homework = null;
        if (request.getHomeworkId() != null) {
            homework = homeworkService.getById(request.getHomeworkId());
            if (homework == null) {
                throw ApiException.notFound("作业不存在");
            }
            boolean exists = homeworkProblemService.lambdaQuery()
                    .eq(HomeworkProblem::getHomeworkId, homework.getId())
                    .eq(HomeworkProblem::getProblemId, problem.getId())
                    .count() > 0;
            if (!exists) {
                throw ApiException.badRequest("该作业中不包含此题目");
            }
        }
        List<ProblemTestcase> testcases = problemTestcaseService.lambdaQuery()
                .eq(ProblemTestcase::getProblemId, problem.getId())
                .list();
        if (testcases.isEmpty()) {
            throw ApiException.badRequest("题目尚未配置测试用例");
        }
        LocalDateTime now = LocalDateTime.now();
        Submission submission = new Submission();
        submission.setStudentId(targetStudentId);
        submission.setProblemId(problem.getId());
        submission.setHomeworkId(request.getHomeworkId());
        submission.setLanguageId(request.getLanguageId());
        submission.setSourceCode(request.getSourceCode());
        submission.setOverallStatusId(1);
        submission.setPassedCaseCount(0);
        submission.setTotalCaseCount(testcases.size());
        submission.setScore(0);
        submission.setCreatedAt(now);
        submission.setUpdatedAt(now);
        submissionService.save(submission);

        boolean systemError = false;
        int passed = 0;
        List<SubmissionTestcaseResult> testcaseResults = new ArrayList<>();
        for (ProblemTestcase testcase : testcases) {
            try {
                Judge0Result result = judge0Client.execute(
                        new Judge0Request(request.getLanguageId(),
                                request.getSourceCode(),
                                testcase.getInputData(),
                                testcase.getOutputData())
                );
                SubmissionTestcaseResult record = buildResultEntity(submission.getId(), testcase.getId(), result);
                testcaseResults.add(record);
                if (isAccepted(result.getStatusId(), result.getStatusDescription())) {
                    passed++;
                }
            } catch (Exception ex) {
                systemError = true;
                SubmissionTestcaseResult record = new SubmissionTestcaseResult();
                record.setSubmissionId(submission.getId());
                record.setTestcaseId(testcase.getId());
                record.setJudge0Token(null);
                record.setStatusId(0);
                record.setMessage("调用判题服务失败: " + ex.getMessage());
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());
                testcaseResults.add(record);
            }
        }
        submissionTestcaseResultService.saveBatch(testcaseResults);

        int overallStatus = resolveOverallStatus(systemError, passed, testcases.size());
        submission.setOverallStatusId(overallStatus);
        submission.setPassedCaseCount(passed);
        submission.setScore(calculateScore(passed, testcases.size()));
        submission.setUpdatedAt(LocalDateTime.now());
        submissionService.updateById(submission);
        updateStatistics(submission);

        return buildDetailVO(submission, testcaseResults);
    }

    private SubmissionTestcaseResult buildResultEntity(Long submissionId, Long testcaseId, Judge0Result result) {
        SubmissionTestcaseResult record = new SubmissionTestcaseResult();
        record.setSubmissionId(submissionId);
        record.setTestcaseId(testcaseId);
        record.setJudge0Token(result.getToken());
        record.setStatusId(result.getStatusId());
        record.setStdout(result.getStdout());
        record.setStderr(result.getStderr());
        record.setCompileOutput(result.getCompileOutput());
        record.setMessage(result.getMessage());
        if (result.getTime() != null) {
            record.setTimeUsed(BigDecimal.valueOf(result.getTime()));
        }
        record.setMemoryUsed(result.getMemory());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return record;
    }

    private boolean isAccepted(Integer statusId, String description) {
        if (statusId != null) {
            return statusId == 3;
        }
        return description != null && description.toLowerCase().contains("accepted");
    }

    private int resolveOverallStatus(boolean systemError, int passed, int total) {
        if (systemError) {
            return 6;
        }
        if (passed == total) {
            return 3;
        }
        if (passed > 0) {
            return 4;
        }
        return 5;
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

    private Problem requireProblem(Long problemId) {
        Problem problem = problemService.getById(problemId);
        if (problem == null) {
            throw ApiException.notFound("题目不存在");
        }
        if (Boolean.FALSE.equals(problem.getIsActive())) {
            throw ApiException.forbidden("题目未上线");
        }
        return problem;
    }

    private Long resolveStudentId(SubmissionCreateRequest request, AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        if ("student".equalsIgnoreCase(current.getRole())) {
            return current.getUserId();
        }
        if ("admin".equalsIgnoreCase(current.getRole())) {
            if (request.getStudentId() == null) {
                throw ApiException.badRequest("管理员提交需指定学生ID");
            }
            Student student = studentService.getById(request.getStudentId());
            if (student == null) {
                throw ApiException.notFound("学生不存在");
            }
            return student.getId();
        }
        throw ApiException.forbidden("仅学生或管理员可以提交代码");
    }

    public SubmissionDetailVO buildDetailVO(Submission submission, List<SubmissionTestcaseResult> testcaseResults) {
        SubmissionDetailVO vo = new SubmissionDetailVO();
        copySubmissionToVO(submission, vo);
        vo.setSourceCode(submission.getSourceCode());
        Map<Integer, SubmissionOverallStatus> overallStatusMap = submissionOverallStatusService
                .list().stream().collect(Collectors.toMap(SubmissionOverallStatus::getId, s -> s));
        SubmissionOverallStatus status = overallStatusMap.get(submission.getOverallStatusId());
        if (status != null) {
            vo.setOverallStatusCode(status.getCode());
            vo.setOverallStatusName(status.getName());
        }
        Map<Integer, String> judgeStatusMap = judgeStatusService.list()
                .stream().collect(Collectors.toMap(JudgeStatus::getId,
                        js -> js.getDescriptionZh() != null ? js.getDescriptionZh() : js.getDescriptionEn()));
        List<SubmissionTestcaseResultVO> details = testcaseResults.stream().map(r -> {
            SubmissionTestcaseResultVO item = new SubmissionTestcaseResultVO();
            item.setTestcaseId(r.getTestcaseId());
            item.setStatusId(r.getStatusId());
            item.setStatusDescription(judgeStatusMap.getOrDefault(r.getStatusId(), r.getMessage()));
            item.setStdout(r.getStdout());
            item.setStderr(r.getStderr());
            item.setCompileOutput(r.getCompileOutput());
            item.setMessage(r.getMessage());
            item.setTimeUsed(r.getTimeUsed());
            item.setMemoryUsed(r.getMemoryUsed());
            return item;
        }).collect(Collectors.toList());
        vo.setTestcaseResults(details);
        return vo;
    }

    private void copySubmissionToVO(Submission submission, SubmissionDetailVO vo) {
        vo.setId(submission.getId());
        vo.setStudentId(submission.getStudentId());
        vo.setProblemId(submission.getProblemId());
        vo.setHomeworkId(submission.getHomeworkId());
        vo.setLanguageId(submission.getLanguageId());
        vo.setOverallStatusId(submission.getOverallStatusId());
        vo.setPassedCaseCount(submission.getPassedCaseCount());
        vo.setTotalCaseCount(submission.getTotalCaseCount());
        vo.setScore(submission.getScore());
        vo.setCreatedAt(submission.getCreatedAt());
        vo.setUpdatedAt(submission.getUpdatedAt());
    }

    private void updateStatistics(Submission submission) {
        boolean accepted = submission.getOverallStatusId() != null && submission.getOverallStatusId() == 3;
        Problem problem = problemService.getById(submission.getProblemId());
        if (problem != null) {
            problem.setSubmitCount(safeCount(problem.getSubmitCount()) + 1);
            problem.setAcCount(safeCount(problem.getAcCount()) + (accepted ? 1 : 0));
            problemService.updateById(problem);
        }
        if (submission.getHomeworkId() != null) {
            HomeworkProblem hp = homeworkProblemService.lambdaQuery()
                    .eq(HomeworkProblem::getHomeworkId, submission.getHomeworkId())
                    .eq(HomeworkProblem::getProblemId, submission.getProblemId())
                    .one();
            if (hp != null) {
                hp.setSubmitCount(safeCount(hp.getSubmitCount()) + 1);
                hp.setAcCount(safeCount(hp.getAcCount()) + (accepted ? 1 : 0));
                homeworkProblemService.updateById(hp);
            }
        }
        Student student = studentService.getById(submission.getStudentId());
        if (student != null) {
            student.setSubmit(safeCount(student.getSubmit()) + 1);
            student.setAc(safeCount(student.getAc()) + (accepted ? 1 : 0));
            if (accepted) {
                student.setScore(safeCount(student.getScore()) + 5);
            }
            studentService.updateById(student);
        }
    }

    private int safeCount(Integer count) {
        return count == null ? 0 : count;
    }
}
