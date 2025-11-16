package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Solution;
import com.oj.onlinejudge.mapper.SolutionMapper;
import com.oj.onlinejudge.service.SolutionService;
import org.springframework.stereotype.Service;

@Service
public class SolutionServiceImpl extends ServiceImpl<SolutionMapper, Solution> implements SolutionService {
}
