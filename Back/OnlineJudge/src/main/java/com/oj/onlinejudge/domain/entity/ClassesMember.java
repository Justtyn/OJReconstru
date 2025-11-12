package com.oj.onlinejudge.domain.entity;

// 班级成员关联实体：映射表 classes_member，关联学生/教师与班级

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@TableName("classes_member")
@Schema(description = "班级成员关系记录")
public class ClassesMember {
    @TableId
    @Schema(description = "主键ID")
    private Long id;
    @TableField("class_id")
    @Schema(description = "班级ID")
    private Long classId;
    @TableField("member_type")
    @Schema(description = "成员类型：student/teacher")
    private String memberType; // student / teacher
    @TableField("student_id")
    @Schema(description = "学生ID（member_type=student时有效）")
    private Long studentId;
    @TableField("teacher_id")
    @Schema(description = "教师ID（member_type=teacher时有效）")
    private Long teacherId;
    @TableField("joined_at")
    @Schema(description = "加入时间")
    private LocalDateTime joinedAt;
    @TableField("left_at")
    @Schema(description = "离开时间（仍在班级则为空）")
    private LocalDateTime leftAt;
}
