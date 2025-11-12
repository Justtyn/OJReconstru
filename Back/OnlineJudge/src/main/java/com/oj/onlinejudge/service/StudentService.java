package com.oj.onlinejudge.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.oj.onlinejudge.domain.entity.Student;

import java.util.Optional;

public interface StudentService extends IService<Student> {
    // 通过用户名查找学生（登录使用）
    Optional<Student> findByUsername(String username);
}
