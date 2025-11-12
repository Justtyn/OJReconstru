package com.oj.onlinejudge.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Teacher;
import com.oj.onlinejudge.mapper.TeacherMapper;
import com.oj.onlinejudge.service.TeacherService;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
}
