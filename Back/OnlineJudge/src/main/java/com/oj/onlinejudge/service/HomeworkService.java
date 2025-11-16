package com.oj.onlinejudge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oj.onlinejudge.domain.entity.Homework;
import java.util.List;

public interface HomeworkService extends IService<Homework> {

    Homework createHomework(Homework homework, List<Long> problemIds);

    Homework updateHomework(Homework homework, List<Long> problemIds);

    void addProblems(Long homeworkId, List<Long> problemIds);

    boolean removeProblem(Long homeworkId, Long problemId);
}
