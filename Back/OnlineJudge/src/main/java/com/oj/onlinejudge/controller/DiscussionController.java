package com.oj.onlinejudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.domain.dto.DiscussionCommentRequest;
import com.oj.onlinejudge.domain.dto.DiscussionRequest;
import com.oj.onlinejudge.domain.dto.group.UpdateGroup;
import com.oj.onlinejudge.domain.entity.Discussion;
import com.oj.onlinejudge.domain.entity.DiscussionComment;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.exception.ApiException;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.DiscussionCommentService;
import com.oj.onlinejudge.service.DiscussionService;
import com.oj.onlinejudge.service.ProblemService;
import com.oj.onlinejudge.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;
    private final DiscussionCommentService discussionCommentService;
    private final ProblemService problemService;
    private final StudentService studentService;

    @Operation(summary = "讨论-列表")
    @GetMapping
    public ApiResponse<Page<Discussion>> list(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "关联题目过滤") @RequestParam(required = false) Long problemId,
            @Parameter(description = "作者过滤") @RequestParam(required = false) Long userId,
            @Parameter(description = "管理员专用：是否包含未启用讨论") @RequestParam(defaultValue = "false") boolean includeInactive) {
        ensureViewer(current);
        LambdaQueryWrapper<Discussion> wrapper = new LambdaQueryWrapper<>();
        if (problemId != null) {
            wrapper.eq(Discussion::getProblemId, problemId);
        }
        if (userId != null) {
            wrapper.eq(Discussion::getUserId, userId);
        }
        boolean includeInactiveAll = includeInactive || isAdmin(current);
        if (!includeInactiveAll) {
            wrapper.eq(Discussion::getIsActive, true);
        }
        wrapper.orderByDesc(Discussion::getUpdateTime)
                .orderByDesc(Discussion::getCreateTime);
        Page<Discussion> result = discussionService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    @Operation(summary = "讨论-详情")
    @GetMapping("/{id}")
    public ApiResponse<Discussion> detail(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureViewer(current);
        Discussion discussion = discussionService.getById(id);
        if (discussion == null) {
            throw ApiException.notFound("讨论不存在");
        }
        if (!Boolean.TRUE.equals(discussion.getIsActive()) && !isAdmin(current) && !isOwner(current, discussion)) {
            throw ApiException.notFound("讨论不存在");
        }
        return ApiResponse.success(discussion);
    }

    @Operation(summary = "讨论-创建（学生/管理员）", description = "管理员发布需指定 authorId 为学生ID")
    @PostMapping
    public ApiResponse<Discussion> create(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Valid @RequestBody DiscussionRequest request) {
        ensureStudentOrAdmin(current);
        Discussion discussion = new Discussion();
        BeanUtils.copyProperties(request, discussion);
        discussion.setId(null);
        discussion.setUserId(resolveAuthorId(current, request.getAuthorId()));
        discussion.setCreateTime(LocalDateTime.now());
        discussion.setUpdateTime(LocalDateTime.now());
        if (discussion.getIsActive() == null) {
            discussion.setIsActive(true);
        }
        if (discussion.getProblemId() != null) {
            ensureProblemExists(discussion.getProblemId());
        }
        discussionService.save(discussion);
        return ApiResponse.success("创建成功", discussion);
    }

    @Operation(summary = "讨论-更新（作者或管理员）")
    @PutMapping("/{id}")
    public ApiResponse<Discussion> update(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id,
            @Validated(UpdateGroup.class) @RequestBody DiscussionRequest request) {
        ensureViewer(current);
        Discussion existing = discussionService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("讨论不存在");
        }
        ensureOwnerOrAdmin(current, existing);
        if (request.getProblemId() != null) {
            ensureProblemExists(request.getProblemId());
            existing.setProblemId(request.getProblemId());
        }
        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            existing.setContent(request.getContent());
        }
        if (request.getIsActive() != null) {
            if (!isAdmin(current)) {
                throw ApiException.forbidden("仅管理员可修改启用状态");
            }
            existing.setIsActive(request.getIsActive());
        }
        existing.setUpdateTime(LocalDateTime.now());
        discussionService.updateById(existing);
        return ApiResponse.success("更新成功", existing);
    }

    @Operation(summary = "讨论-删除（作者或管理员）")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureViewer(current);
        Discussion existing = discussionService.getById(id);
        if (existing == null) {
            throw ApiException.notFound("讨论不存在");
        }
        ensureOwnerOrAdmin(current, existing);
        discussionService.removeById(id);
        return ApiResponse.success(null);
    }

    @Operation(summary = "讨论评论-列表")
    @GetMapping("/{id}/comments")
    public ApiResponse<List<DiscussionComment>> listComments(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id) {
        ensureViewer(current);
        Discussion discussion = ensureVisibleDiscussion(id, current);
        List<DiscussionComment> comments = discussionCommentService.lambdaQuery()
                .eq(DiscussionComment::getDiscussId, discussion.getId())
                .orderByAsc(DiscussionComment::getCreateTime)
                .list();
        return ApiResponse.success(comments);
    }

    @Operation(summary = "讨论评论-创建（学生）")
    @PostMapping("/{id}/comments")
    public ApiResponse<DiscussionComment> createComment(
            @Parameter(description = "当前学生") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long id,
            @Valid @RequestBody DiscussionCommentRequest request) {
        ensureStudent(current);
        Discussion discussion = ensureVisibleDiscussion(id, current);
        DiscussionComment comment = new DiscussionComment();
        comment.setDiscussId(discussion.getId());
        comment.setUserId(current.getUserId());
        comment.setContent(request.getContent());
        comment.setCreateTime(LocalDateTime.now());
        discussionCommentService.save(comment);
        return ApiResponse.success("发布成功", comment);
    }

    @Operation(summary = "讨论评论-删除（作者或管理员）")
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @Parameter(description = "当前登录用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @PathVariable Long commentId) {
        ensureViewer(current);
        DiscussionComment comment = discussionCommentService.getById(commentId);
        if (comment == null) {
            throw ApiException.notFound("评论不存在");
        }
        if (!(isAdmin(current) || (isStudent(current) && Objects.equals(comment.getUserId(), current.getUserId())))) {
            throw ApiException.forbidden("无权删除该评论");
        }
        discussionCommentService.removeById(commentId);
        return ApiResponse.success(null);
    }

    private Discussion ensureVisibleDiscussion(Long id, AuthenticatedUser current) {
        Discussion discussion = discussionService.getById(id);
        if (discussion == null) {
            throw ApiException.notFound("讨论不存在");
        }
        if (!Boolean.TRUE.equals(discussion.getIsActive()) && !isAdmin(current) && !isOwner(current, discussion)) {
            throw ApiException.notFound("讨论不存在");
        }
        return discussion;
    }

    private void ensureProblemExists(Long problemId) {
        Problem problem = problemService.getById(problemId);
        if (problem == null) {
            throw ApiException.notFound("题目不存在");
        }
    }

    private void ensureViewer(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        String role = current.getRole() == null ? "" : current.getRole().toLowerCase();
        if (!"student".equals(role) && !"teacher".equals(role) && !"admin".equals(role)) {
            throw ApiException.forbidden("无权限访问讨论");
        }
    }

    private void ensureStudent(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        if (!"student".equalsIgnoreCase(current.getRole())) {
            throw ApiException.forbidden("仅学生可执行此操作");
        }
    }

    private void ensureStudentOrAdmin(AuthenticatedUser current) {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        String role = current.getRole() == null ? "" : current.getRole().toLowerCase();
        if (!"student".equals(role) && !"admin".equals(role)) {
            throw ApiException.forbidden("仅学生或管理员可执行此操作");
        }
    }

    private Long resolveAuthorId(AuthenticatedUser current, Long requestedAuthorId) {
        boolean admin = isAdmin(current);
        if (admin) {
            if (requestedAuthorId == null) {
                throw ApiException.badRequest("管理员发布讨论需指定 authorId（学生ID）");
            }
            ensureStudentExists(requestedAuthorId);
            return requestedAuthorId;
        }
        ensureStudentExists(current.getUserId());
        return current.getUserId();
    }

    private boolean isAdmin(AuthenticatedUser current) {
        return current != null && "admin".equalsIgnoreCase(current.getRole());
    }

    private boolean isStudent(AuthenticatedUser current) {
        return current != null && "student".equalsIgnoreCase(current.getRole());
    }

    private boolean isOwner(AuthenticatedUser current, Discussion discussion) {
        return current != null
                && "student".equalsIgnoreCase(current.getRole())
                && discussion.getUserId() != null
                && discussion.getUserId().equals(current.getUserId());
    }

    private void ensureOwnerOrAdmin(AuthenticatedUser current, Discussion discussion) {
        if (!(isAdmin(current) || isOwner(current, discussion))) {
            throw ApiException.forbidden("无权操作该讨论");
        }
    }

    private void ensureStudentExists(Long studentId) {
        if (studentId == null || studentService.getById(studentId) == null) {
            throw ApiException.badRequest("指定的学生不存在");
        }
    }
}
