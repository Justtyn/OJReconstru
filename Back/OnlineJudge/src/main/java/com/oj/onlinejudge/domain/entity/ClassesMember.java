package com.oj.onlinejudge.domain.entity;

// 班级成员关联实体：映射表 classes_member，关联学生/教师与班级

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.Data;

@Data
@TableName("classes_member")
@Schema(name = "ClassesMember", description = "班级成员关联实体")
public class ClassesMember {
    @TableId
    @Schema(description = "记录ID")
    private Long id;
    @TableField("class_id")
    @Schema(description = "班级ID")
    private Long classId;
    @TableField("member_type")
    @Schema(description = "成员类型: student/teacher")
    private String memberType; // student / teacher
    @TableField("student_id")
    @Schema(description = "学生ID")
    private Long studentId;
    @TableField("teacher_id")
    @Schema(description = "教师ID")
    private Long teacherId;
    @TableField("joined_at")
    @Schema(description = "加入时间")
    private LocalDateTime joinedAt;
    @TableField(value = "left_at", updateStrategy = FieldStrategy.IGNORED)
    @Schema(description = "离开时间")
    private LocalDateTime leftAt;
}
