package com.oj.onlinejudge.domain.entity;

// 作业实体：对应 homework 表

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("homework")
@Schema(name = "Homework", description = "作业实体")
public class Homework {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "作业ID")
    private Long id;
    @Schema(description = "标题")
    private String title;
    @TableField("class_id")
    @Schema(description = "所属班级ID")
    private Long classId;
    @TableField("start_time")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    @TableField("end_time")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    @Schema(description = "作业说明")
    private String description;
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    @TableField("is_active")
    @Schema(description = "是否启用")
    private Boolean isActive;
}
