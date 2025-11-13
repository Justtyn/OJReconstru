package com.oj.onlinejudge.domain.entity;

// 班级实体：映射表 classes，含名称、邀请码、起止日期等

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("classes")
@Schema(name = "Classes", description = "班级实体")
public class Classes {
    @TableId
    @Schema(description = "班级ID")
    private Long id;
    @Schema(description = "班级名称")
    private String name;
    @TableField("creator_id")
    @Schema(description = "创建者教师ID")
    private Long creatorId; // teacher id
    @TableField("homework_quantity")
    @Schema(description = "作业数量")
    private Integer homeworkQuantity;
    @Schema(description = "班级邀请码")
    private String code;
    @TableField("start_date")
    @Schema(description = "开始日期")
    private LocalDate startDate;
    @TableField("end_date")
    @Schema(description = "结束日期")
    private LocalDate endDate;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
