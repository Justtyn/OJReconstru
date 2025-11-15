package com.oj.onlinejudge.domain.entity;

// 作业题目关联实体：映射 homework_problem 表

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("homework_problem")
@Schema(name = "HomeworkProblem", description = "作业题目关联实体")
public class HomeworkProblem {
    @TableId(value = "homework_id")
    @Schema(description = "作业ID")
    private Long homeworkId;
    @TableField("problem_id")
    @Schema(description = "题目ID")
    private Long problemId;
    @TableField("ac_count")
    @Schema(description = "通过数量")
    private Integer acCount;
    @TableField("submit_count")
    @Schema(description = "提交数量")
    private Integer submitCount;
}
