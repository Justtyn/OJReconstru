package com.oj.onlinejudge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oj.onlinejudge.domain.entity.JudgeStatus;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JudgeStatusMapper extends BaseMapper<JudgeStatus> {
}
