package com.oj.onlinejudge.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "作业题目批量操作请求")
public class HomeworkProblemBatchRequest {

    @NotEmpty(message = "题目列表不能为空")
    private List<Long> problemIds;
}
