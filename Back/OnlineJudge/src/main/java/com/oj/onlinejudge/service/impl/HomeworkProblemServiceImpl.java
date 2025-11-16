package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.HomeworkProblem;
import com.oj.onlinejudge.mapper.HomeworkProblemMapper;
import com.oj.onlinejudge.service.HomeworkProblemService;
import org.springframework.stereotype.Service;

@Service
public class HomeworkProblemServiceImpl extends ServiceImpl<HomeworkProblemMapper, HomeworkProblem>
        implements HomeworkProblemService {
}
