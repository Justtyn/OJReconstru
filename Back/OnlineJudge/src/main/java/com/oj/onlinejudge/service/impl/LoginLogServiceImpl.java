package com.oj.onlinejudge.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.LoginLog;
import com.oj.onlinejudge.mapper.LoginLogMapper;
import com.oj.onlinejudge.service.LoginLogService;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
    // keep default synchronous behavior
}
