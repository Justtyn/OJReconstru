package com.oj.onlinejudge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(description = "可视化-时间序列点")
public class AnalyticsTimeseriesVO {

    @Schema(description = "时间标签，如 2025-12-12 / 2025-12 / 2025-W51 / all")
    private String label;

    @Schema(description = "总数")
    private long total;

    @Schema(description = "成功/通过数（可选）")
    private long success;

    @Schema(description = "失败数（可选）")
    private long fail;

    @Schema(description = "桶开始时间")
    private LocalDateTime bucketStart;

    @Schema(description = "桶结束时间（开区间，null 表示无上限）")
    private LocalDateTime bucketEnd;

    public AnalyticsTimeseriesVO(String label) {
        this.label = label;
    }

    public AnalyticsTimeseriesVO(String label, LocalDateTime bucketStart, LocalDateTime bucketEnd) {
        this.label = label;
        this.bucketStart = bucketStart;
        this.bucketEnd = bucketEnd;
    }
}
