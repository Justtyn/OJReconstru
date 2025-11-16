package com.oj.onlinejudge.domain.dto;

import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "公告创建/更新请求")
public class AnnouncementRequest {

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "标题不能为空")
    private String title;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "内容不能为空")
    private String content;

    @Schema(description = "发布时间")
    private LocalDateTime time;

    @Schema(description = "是否置顶")
    private Boolean isPinned;

    @Schema(description = "是否启用")
    private Boolean isActive;
}
