package com.oj.onlinejudge.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.mapper.ClassesMapper;
import com.oj.onlinejudge.service.ClassesService;
import org.springframework.stereotype.Service;

@Service
public class ClassesServiceImpl extends ServiceImpl<ClassesMapper, Classes> implements ClassesService {
}
