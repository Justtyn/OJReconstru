package com.oj.onlinejudge.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.mapper.StudentMapper;
import com.oj.onlinejudge.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    // 通过用户名查找学生（登录使用）
    @Override
    public Optional<Student> findByUsername(String username) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUsername, username).last("limit 1");
        return Optional.ofNullable(this.getOne(wrapper, false));
    }
}
