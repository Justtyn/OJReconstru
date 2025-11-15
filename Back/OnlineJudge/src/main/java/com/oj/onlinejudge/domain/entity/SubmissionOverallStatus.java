package com.oj.onlinejudge.domain.entity;

// 提交整体状态实体：映射 submission_overall_status 表

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("submission_overall_status")
@Schema(name = "SubmissionOverallStatus", description = "提交整体状态实体")
public class SubmissionOverallStatus {
    @TableId
    @Schema(description = "状态ID")
    private Integer id;
    @Schema(description = "状态编码")
    private String code;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "描述")
    private String description;
    @TableField("is_success")
    @Schema(description = "是否成功")
    private Boolean isSuccess;
}
