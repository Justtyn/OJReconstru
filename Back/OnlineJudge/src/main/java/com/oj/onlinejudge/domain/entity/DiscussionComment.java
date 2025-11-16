package com.oj.onlinejudge.domain.entity;

// 讨论评论实体：映射 discussion_comment 表，支持楼中楼回复

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.Data;

@Data
@TableName("discussion_comment")
@Schema(name = "DiscussionComment", description = "讨论评论实体")
public class DiscussionComment {
    @TableId
    @Schema(description = "评论ID")
    private Long id;
    @TableField("discuss_id")
    @Schema(description = "所属讨论ID")
    private Long discussId;
    @TableField("user_id")
    @Schema(description = "评论者学生ID")
    private Long userId;
    @Schema(description = "评论内容")
    private String content;
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
