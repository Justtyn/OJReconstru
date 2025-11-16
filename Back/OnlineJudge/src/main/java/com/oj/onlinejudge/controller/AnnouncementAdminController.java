package com.oj.onlinejudge.controller;

import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.AnnouncementRequest;
import com.oj.onlinejudge.domain.dto.group.CreateGroup;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Announcement;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/announcements")
@RequiredArgsConstructor
public class AnnouncementAdminController {

    private final AnnouncementService announcementService;

    @Operation(summary = "公告-创建，仅管理员")
    @PostMapping
    public ApiResponse<Announcement> create(
            @Parameter(description = "当前管理员") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Validated(CreateGroup.class) @RequestBody AnnouncementRequest request) {
        ensureAdmin(current);
        Announcement body = new Announcement();
        BeanUtils.copyProperties(request, body);
        body.setId(null);
        if (body.getTime() == null) {
            body.setTime(LocalDateTime.now());
        }
        if (body.getIsActive() == null) {
            body.setIsActive(true);
        }
        boolean ok = announcementService.save(body);
        if (!ok) {
            throw ApiException.internal("创建公告失败");
        }
        return ApiResponse.success("创建成功", body);
    }

    @Operation(summary = "公告-更新，仅管理员")
    @PutMapping("/{id}")
    public ApiResponse<Announcement> update(
            @Parameter(description = "当前管理员") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id,
            @Validated(UpdateGroup.class) @RequestBody AnnouncementRequest request) {
        ensureAdmin(current);
        Announcement existing = announcementService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("公告不存在");
        }
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        if (request.getTime() != null) {
            existing.setTime(request.getTime());
        }
        if (request.getIsPinned() != null) {
            existing.setIsPinned(request.getIsPinned());
        }
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }
        boolean ok = announcementService.updateById(existing);
        if (!ok) {
            throw ApiException.internal("更新公告失败");
        }
        return ApiResponse.success("更新成功", existing);
    }

    @Operation(summary = "公告-删除，仅管理员")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前管理员") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureAdmin(current);
        Announcement existing = announcementService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("公告不存在");
        }
        announcementService.removeById(id);
        return ApiResponse.success(null);
    }

    private void ensureAdmin(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        if (!"admin".equalsIgnoreCase(current.getRole())) {
            throw ApiException.forbidden("仅管理员可执行此操作");
        }
    }
}
