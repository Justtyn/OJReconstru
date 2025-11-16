package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.ProblemTestcase;
import com.oj.onlinejudge.mapper.ProblemMapper;
import com.oj.onlinejudge.service.ProblemService;
import com.oj.onlinejudge.service.ProblemTestcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {

    private final ProblemTestcaseService problemTestcaseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeProblemWithTestcases(Long problemId) {
        problemTestcaseService.lambdaUpdate()
                .eq(ProblemTestcase::getProblemId, problemId)
                .remove();
        return this.removeById(problemId);
    }
}
