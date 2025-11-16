package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.SubmissionOverallStatus;
import com.oj.onlinejudge.mapper.SubmissionOverallStatusMapper;
import com.oj.onlinejudge.service.SubmissionOverallStatusService;
import org.springframework.stereotype.Service;

@Service
public class SubmissionOverallStatusServiceImpl
        extends ServiceImpl<SubmissionOverallStatusMapper, SubmissionOverallStatus>
        implements SubmissionOverallStatusService {
}
