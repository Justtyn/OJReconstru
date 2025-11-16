package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Discussion;
import com.oj.onlinejudge.mapper.DiscussionMapper;
import com.oj.onlinejudge.service.DiscussionService;
import org.springframework.stereotype.Service;

@Service
public class DiscussionServiceImpl extends ServiceImpl<DiscussionMapper, Discussion> implements DiscussionService {
}
