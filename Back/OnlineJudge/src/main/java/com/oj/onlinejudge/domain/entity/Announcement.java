package com.oj.onlinejudge.domain.entity;

// 公告实体：映射公告表 announcement

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.Data;

@Data
@TableName("announcement")
@Schema(name = "Announcement", description = "公告实体")
public class Announcement {
    @TableId
    @Schema(description = "公告ID")
    private Long id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容")
    private String content;
    @TableField("time")
    @Schema(description = "发布时间")
    private LocalDateTime time;
    @TableField("is_pinned")
    @Schema(description = "是否置顶")
    private Boolean isPinned;
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    @TableField("is_active")
    @Schema(description = "是否启用")
    private Boolean isActive;
}
