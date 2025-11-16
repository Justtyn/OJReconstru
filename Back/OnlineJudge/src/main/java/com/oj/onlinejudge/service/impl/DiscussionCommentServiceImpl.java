package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.DiscussionComment;
import com.oj.onlinejudge.mapper.DiscussionCommentMapper;
import com.oj.onlinejudge.service.DiscussionCommentService;
import org.springframework.stereotype.Service;

@Service
public class DiscussionCommentServiceImpl extends ServiceImpl<DiscussionCommentMapper, DiscussionComment>
        implements DiscussionCommentService {
}
