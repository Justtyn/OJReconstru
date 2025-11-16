package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.ProblemTestcase;
import com.oj.onlinejudge.mapper.ProblemTestcaseMapper;
import com.oj.onlinejudge.service.ProblemTestcaseService;
import org.springframework.stereotype.Service;

@Service
public class ProblemTestcaseServiceImpl extends ServiceImpl<ProblemTestcaseMapper, ProblemTestcase>
        implements ProblemTestcaseService {
}
