package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.SubmissionTestcaseResult;
import com.oj.onlinejudge.mapper.SubmissionTestcaseResultMapper;
import com.oj.onlinejudge.service.SubmissionTestcaseResultService;
import org.springframework.stereotype.Service;

@Service
public class SubmissionTestcaseResultServiceImpl
        extends ServiceImpl<SubmissionTestcaseResultMapper, SubmissionTestcaseResult>
        implements SubmissionTestcaseResultService {
}
