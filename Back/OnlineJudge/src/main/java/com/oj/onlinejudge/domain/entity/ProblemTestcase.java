package com.oj.onlinejudge.domain.entity;

// 题目测试用例实体：映射 problem_testcase 表

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("problem_testcase")
@Schema(name = "ProblemTestcase", description = "题目测试用例实体")
public class ProblemTestcase {
    @TableId
    @Schema(description = "测试用例ID")
    private Long id;
    @TableField("problem_id")
    @Schema(description = "题目ID")
    private Long problemId;
    @TableField("input_data")
    @Schema(description = "输入数据")
    private String inputData;
    @TableField("output_data")
    @Schema(description = "输出数据")
    private String outputData;
}
