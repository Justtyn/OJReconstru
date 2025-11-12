package com.oj.onlinejudge.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Admin;
import com.oj.onlinejudge.mapper.AdminMapper;
import com.oj.onlinejudge.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}
