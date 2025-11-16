package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.JudgeStatus;
import com.oj.onlinejudge.mapper.JudgeStatusMapper;
import com.oj.onlinejudge.service.JudgeStatusService;
import org.springframework.stereotype.Service;

@Service
public class JudgeStatusServiceImpl extends ServiceImpl<JudgeStatusMapper, JudgeStatus>
        implements JudgeStatusService {
}
