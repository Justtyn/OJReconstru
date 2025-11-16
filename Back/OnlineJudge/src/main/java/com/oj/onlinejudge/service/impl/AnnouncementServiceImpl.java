package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Announcement;
import com.oj.onlinejudge.mapper.AnnouncementMapper;
import com.oj.onlinejudge.service.AnnouncementService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
        implements AnnouncementService {
}
