package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.mapper.StudentMapper;
import com.oj.onlinejudge.service.StudentService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Override
    public Optional<Student> findByUsername(String username) {
        if (username == null || username.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.lambdaQuery()
            .eq(Student::getUsername, username)
            .last("limit 1")
            .one());
    }
}
