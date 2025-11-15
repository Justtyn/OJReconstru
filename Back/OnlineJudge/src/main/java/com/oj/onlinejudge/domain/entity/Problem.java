package com.oj.onlinejudge.domain.entity;

// 题目实体：映射 problem 表

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("problem")
@Schema(name = "Problem", description = "题目实体")
public class Problem {
    @TableId
    @Schema(description = "题目ID")
    private Long id;
    @Schema(description = "题目名称")
    private String name;
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @TableField("ac_count")
    @Schema(description = "通过次数")
    private Integer acCount;
    @TableField("submit_count")
    @Schema(description = "提交次数")
    private Integer submitCount;
    @TableField("desc")
    @Schema(description = "题目描述")
    private String description;
    @TableField("desc_input")
    @Schema(description = "输入描述")
    private String descriptionInput;
    @TableField("desc_output")
    @Schema(description = "输出描述")
    private String descriptionOutput;
    @TableField("sample_input")
    @Schema(description = "输入样例")
    private String sampleInput;
    @TableField("sample_output")
    @Schema(description = "输出样例")
    private String sampleOutput;
    @Schema(description = "提示")
    private String hint;
    @TableField("daily_challenge")
    @Schema(description = "日常挑战标识")
    private String dailyChallenge;
    @Schema(description = "难度")
    private String difficulty;
    @TableField("time_limit_ms")
    @Schema(description = "时间限制(ms)")
    private Integer timeLimitMs;
    @TableField("memory_limit_kb")
    @Schema(description = "内存限制(KB)")
    private Integer memoryLimitKb;
    @Schema(description = "题目来源")
    private String source;
    @TableField("is_active")
    @Schema(description = "是否启用")
    private Boolean isActive;
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
