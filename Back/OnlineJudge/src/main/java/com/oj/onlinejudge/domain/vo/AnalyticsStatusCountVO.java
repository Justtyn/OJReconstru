package com.oj.onlinejudge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "可视化-状态计数")
public class AnalyticsStatusCountVO {

    @Schema(description = "状态ID/枚举")
    private Integer statusId;

    @Schema(description = "数量")
    private Long total;

    @Schema(description = "状态名称或别名（可选）")
    private String name;

    @Schema(description = "状态编码/别名（可选）")
    private String code;

    @Schema(description = "占比（0-1），便于直接绘制饼图")
    private Double percentage;

    public AnalyticsStatusCountVO(Integer statusId, Long total, String name) {
        this.statusId = statusId;
        this.total = total;
        this.name = name;
    }

    public AnalyticsStatusCountVO(Integer statusId, Long total, String name, String code) {
        this(statusId, total, name);
        this.code = code;
    }
}
