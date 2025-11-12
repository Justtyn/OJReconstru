package com.oj.onlinejudge.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oj.onlinejudge.domain.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
