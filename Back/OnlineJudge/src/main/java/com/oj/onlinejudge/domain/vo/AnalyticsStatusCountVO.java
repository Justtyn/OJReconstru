package com.oj.onlinejudge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "可视化-状态计数")
public class AnalyticsStatusCountVO {

    @Schema(description = "状态ID/枚举")
    private Integer statusId;

    @Schema(description = "数量")
    private Long total;

    @Schema(description = "状态名称或别名（可选）")
    private String name;
}
