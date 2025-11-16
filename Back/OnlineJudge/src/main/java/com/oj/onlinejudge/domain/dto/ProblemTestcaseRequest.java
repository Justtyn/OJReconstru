package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "题目测试用例创建/更新请求")
public class ProblemTestcaseRequest {

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "输入数据不能为空")
    private String inputData;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "输出数据不能为空")
    private String outputData;
}
