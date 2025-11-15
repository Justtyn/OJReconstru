package com.oj.onlinejudge.domain.entity;

// 判题状态实体：映射 judge_status 表

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("judge_status")
@Schema(name = "JudgeStatus", description = "判题状态实体")
public class JudgeStatus {
    @TableId
    @Schema(description = "状态ID")
    private Integer id;
    @Schema(description = "内部编码")
    private String code;
    @TableField("description_en")
    @Schema(description = "英文描述")
    private String descriptionEn;
    @TableField("description_zh")
    @Schema(description = "中文描述")
    private String descriptionZh;
    @TableField("is_final")
    @Schema(description = "是否终态")
    private Boolean isFinal;
    @TableField("is_success")
    @Schema(description = "是否为通过状态")
    private Boolean isSuccess;
    @TableField("is_compile_error")
    @Schema(description = "是否编译错误")
    private Boolean isCompileError;
    @TableField("is_judge_error")
    @Schema(description = "是否判题内部错误")
    private Boolean isJudgeError;
    @TableField("sort_order")
    @Schema(description = "排序值")
    private Integer sortOrder;
}
