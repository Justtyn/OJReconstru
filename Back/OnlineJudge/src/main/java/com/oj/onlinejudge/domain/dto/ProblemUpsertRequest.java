package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "题目创建/更新请求体")
public class ProblemUpsertRequest {

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "题目名称不能为空")
    private String name;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "题目描述不能为空")
    private String description;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "输入描述不能为空")
    private String descriptionInput;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "输出描述不能为空")
    private String descriptionOutput;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "请输入样例输入")
    private String sampleInput;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "请输入样例输出")
    private String sampleOutput;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "提示信息不能为空")
    private String hint;

    private String dailyChallenge;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "请指定难度")
    @Pattern(groups = {CreateGroup.class, UpdateGroup.class},
            regexp = "easy|medium|hard",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "难度只能是 easy/medium/hard")
    private String difficulty;

    @Min(value = 1, groups = {CreateGroup.class, UpdateGroup.class}, message = "时间限制必须大于0")
    private Integer timeLimitMs;

    @Min(value = 1, groups = {CreateGroup.class, UpdateGroup.class}, message = "内存限制必须大于0")
    private Integer memoryLimitKb;

    private String source;

    private Boolean isActive;
}
