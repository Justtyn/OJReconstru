package com.oj.onlinejudge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oj.onlinejudge.domain.entity.Problem;

public interface ProblemService extends IService<Problem> {

    /**
     * 删除题目并同步移除关联的测试用例
     *
     * @param problemId 题目ID
     * @return 删除是否成功
     */
    boolean removeProblemWithTestcases(Long problemId);
}
