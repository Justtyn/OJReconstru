package com.oj.onlinejudge.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.ClassesMember;
import com.oj.onlinejudge.mapper.ClassesMemberMapper;
import com.oj.onlinejudge.service.ClassesMemberService;
import org.springframework.stereotype.Service;

@Service
public class ClassesMemberServiceImpl extends ServiceImpl<ClassesMemberMapper, ClassesMember> implements ClassesMemberService {
}
