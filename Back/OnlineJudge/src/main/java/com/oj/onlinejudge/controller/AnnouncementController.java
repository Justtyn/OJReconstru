package com.oj.onlinejudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.entity.Announcement;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @Operation(summary = "公告-列表", description = "学生/教师/管理员均可查看")
    @GetMapping
    public ApiResponse<Page<Announcement>> list(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "是否只看置顶") @RequestParam(required = false) Boolean pinnedOnly,
            @Parameter(description = "标题关键字") @RequestParam(required = false) String keyword) {
        ensureLoggedIn(current);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getIsActive, true);
        if (Boolean.TRUE.equals(pinnedOnly)) {
            wrapper.eq(Announcement::getIsPinned, true);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Announcement::getTitle, keyword);
        }
        wrapper.orderByDesc(Announcement::getIsPinned)
                .orderByDesc(Announcement::getTime)
                .orderByDesc(Announcement::getUpdatedAt);
        Page<Announcement> result = announcementService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    @Operation(summary = "公告-详情")
    @GetMapping("/{id}")
    public ApiResponse<Announcement> detail(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureLoggedIn(current);
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            throw ApiException.notFound("公告不存在");
        }
        if (!Boolean.TRUE.equals(announcement.getIsActive()) && !isAdmin(current)) {
            throw ApiException.notFound("公告不存在");
        }
        return ApiResponse.success(announcement);
    }

    private void ensureLoggedIn(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
    }

    private boolean isAdmin(AuthenticatedUser current) {
        return current != null && Objects.equals("admin", current.getRole());
    }
}
