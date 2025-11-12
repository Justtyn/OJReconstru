package com.oj.onlinejudge.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oj.onlinejudge.domain.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
