package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Homework;
import com.oj.onlinejudge.domain.entity.HomeworkProblem;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.mapper.HomeworkMapper;
import com.oj.onlinejudge.service.HomeworkProblemService;
import com.oj.onlinejudge.service.HomeworkService;
import com.oj.onlinejudge.service.ProblemService;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements HomeworkService {

    private final HomeworkProblemService homeworkProblemService;
    private final ProblemService problemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Homework createHomework(Homework homework, List<Long> problemIds) {
        LocalDateTime now = LocalDateTime.now();
        if (homework.getCreatedAt() == null) {
            homework.setCreatedAt(now);
        }
        homework.setUpdatedAt(now);
        if (homework.getIsActive() == null) {
            homework.setIsActive(true);
        }
        this.save(homework);
        if (homework.getId() == null) {
            throw ApiException.internal("作业ID生成失败");
        }
        if (!CollectionUtils.isEmpty(problemIds)) {
            validateProblems(problemIds);
            replaceHomeworkProblemsInternal(homework.getId(), new LinkedHashSet<>(problemIds));
        }
        return homework;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Homework updateHomework(Homework homework, List<Long> problemIds) {
        homework.setUpdatedAt(LocalDateTime.now());
        boolean ok = this.updateById(homework);
        if (!ok) {
            throw ApiException.internal("更新作业失败");
        }
        if (problemIds != null) {
            if (!problemIds.isEmpty()) {
                validateProblems(problemIds);
                replaceHomeworkProblemsInternal(homework.getId(), new LinkedHashSet<>(problemIds));
            } else {
                homeworkProblemService.lambdaUpdate()
                        .eq(HomeworkProblem::getHomeworkId, homework.getId())
                        .remove();
            }
        }
        return homework;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProblems(Long homeworkId, List<Long> problemIds) {
        if (CollectionUtils.isEmpty(problemIds)) {
            throw ApiException.badRequest("题目列表不能为空");
        }
        validateProblems(problemIds);
        Set<Long> existing = homeworkProblemService.lambdaQuery()
                .eq(HomeworkProblem::getHomeworkId, homeworkId)
                .list()
                .stream()
                .map(HomeworkProblem::getProblemId)
                .collect(Collectors.toSet());
        List<HomeworkProblem> additions = problemIds.stream()
                .filter(pid -> !existing.contains(pid))
                .distinct()
                .map(pid -> buildHomeworkProblem(homeworkId, pid))
                .collect(Collectors.toList());
        if (additions.isEmpty()) {
            return;
        }
        homeworkProblemService.saveBatch(additions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeProblem(Long homeworkId, Long problemId) {
        return homeworkProblemService.lambdaUpdate()
                .eq(HomeworkProblem::getHomeworkId, homeworkId)
                .eq(HomeworkProblem::getProblemId, problemId)
                .remove();
    }

    private void validateProblems(Collection<Long> problemIds) {
        if (CollectionUtils.isEmpty(problemIds)) {
            return;
        }
        Set<Long> unique = new LinkedHashSet<>(problemIds);
        if (unique.isEmpty()) {
            return;
        }
        List<Problem> problems = problemService.listByIds(unique);
        if (problems.size() != unique.size()) {
            throw ApiException.badRequest("题目列表包含不存在的记录");
        }
    }

    private void replaceHomeworkProblemsInternal(Long homeworkId, Set<Long> problemIds) {
        homeworkProblemService.lambdaUpdate()
                .eq(HomeworkProblem::getHomeworkId, homeworkId)
                .remove();
        if (CollectionUtils.isEmpty(problemIds)) {
            return;
        }
        List<HomeworkProblem> batch = problemIds.stream()
                .map(pid -> buildHomeworkProblem(homeworkId, pid))
                .collect(Collectors.toList());
        if (!batch.isEmpty()) {
            homeworkProblemService.saveBatch(batch);
        }
    }

    private HomeworkProblem buildHomeworkProblem(Long homeworkId, Long problemId) {
        HomeworkProblem hp = new HomeworkProblem();
        hp.setHomeworkId(homeworkId);
        hp.setProblemId(problemId);
        hp.setAcCount(0);
        hp.setSubmitCount(0);
        return hp;
    }
}
