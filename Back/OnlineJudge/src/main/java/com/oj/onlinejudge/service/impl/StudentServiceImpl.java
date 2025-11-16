package com.oj.onlinejudge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.domain.entity.ClassesMember;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.mapper.StudentMapper;
import com.oj.onlinejudge.service.ClassesMemberService;
import com.oj.onlinejudge.service.ClassesService;
import com.oj.onlinejudge.service.StudentService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final ClassesService classesService;
    private final ClassesMemberService classesMemberService;

    public StudentServiceImpl(ClassesService classesService, ClassesMemberService classesMemberService) {
        this.classesService = classesService;
        this.classesMemberService = classesMemberService;
    }

    @Override
    public Optional<Student> findByUsername(String username) {
        if (username == null || username.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.lambdaQuery()
                .eq(Student::getUsername, username)
                .last("limit 1")
                .one());
    }

    @Override
    @Transactional
    public Classes joinClass(Long studentId, String classCode) {
        if (studentId == null) {
            throw ApiException.badRequest("学生ID不能为空");
        }
        if (!StringUtils.hasText(classCode)) {
            throw ApiException.badRequest("班级邀请码不能为空");
        }
        Student student = getById(studentId);
        if (student == null) {
            throw ApiException.notFound("学生不存在");
        }
        Classes target = classesService.lambdaQuery()
                .eq(Classes::getCode, classCode)
                .last("limit 1")
                .one();
        if (target == null) {
            throw ApiException.notFound("班级不存在或邀请码无效");
        }
        ClassesMember active = classesMemberService.lambdaQuery()
                .eq(ClassesMember::getStudentId, studentId)
                .eq(ClassesMember::getMemberType, "student")
                .isNull(ClassesMember::getLeftAt)
                .last("limit 1")
                .one();
        if (active != null) {
            if (active.getClassId() != null && active.getClassId().equals(target.getId())) {
                return target;
            }
            throw ApiException.conflict("已加入其他班级");
        }
        ClassesMember history = classesMemberService.lambdaQuery()
                .eq(ClassesMember::getStudentId, studentId)
                .eq(ClassesMember::getMemberType, "student")
                .orderByDesc(ClassesMember::getJoinedAt)
                .last("limit 1")
                .one();
        if (history != null) {
            history.setClassId(target.getId());
            history.setJoinedAt(LocalDateTime.now());
            history.setLeftAt(null);
            classesMemberService.updateById(history);
            return target;
        }
        ClassesMember cm = new ClassesMember();
        cm.setClassId(target.getId());
        cm.setMemberType("student");
        cm.setStudentId(studentId);
        cm.setJoinedAt(LocalDateTime.now());
        classesMemberService.save(cm);
        return target;
    }

    @Override
    public Classes getJoinedClass(Long studentId) {
        if (studentId == null) {
            throw ApiException.badRequest("学生ID不能为空");
        }
        Student student = getById(studentId);
        if (student == null) {
            throw ApiException.notFound("学生不存在");
        }
        ClassesMember member = classesMemberService.lambdaQuery()
                .eq(ClassesMember::getStudentId, studentId)
                .eq(ClassesMember::getMemberType, "student")
                .isNull(ClassesMember::getLeftAt)
                .last("limit 1")
                .one();
        if (member == null || member.getClassId() == null) {
            throw ApiException.notFound("尚未加入班级");
        }
        Classes classes = classesService.getById(member.getClassId());
        if (classes == null) {
            throw ApiException.notFound("班级不存在");
        }
        return classes;
    }

    @Override
    @Transactional
    public void leaveClass(Long studentId) {
        if (studentId == null) {
            throw ApiException.badRequest("学生ID不能为空");
        }
        ClassesMember member = classesMemberService.lambdaQuery()
                .eq(ClassesMember::getStudentId, studentId)
                .eq(ClassesMember::getMemberType, "student")
                .isNull(ClassesMember::getLeftAt)
                .last("limit 1")
                .one();
        if (member == null) {
            throw ApiException.notFound("尚未加入班级");
        }
        member.setLeftAt(LocalDateTime.now());
        classesMemberService.updateById(member);
    }
}
