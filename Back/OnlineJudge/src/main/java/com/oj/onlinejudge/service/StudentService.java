package com.oj.onlinejudge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.domain.entity.Student;

import java.util.Optional;


public interface StudentService extends IService<Student> {
    // 根据用户名查找学生
    Optional<Student> findByUsername(String username);

    Classes joinClass(Long studentId, String classCode);

    Classes getJoinedClass(Long studentId);

    void leaveClass(Long studentId);
}
