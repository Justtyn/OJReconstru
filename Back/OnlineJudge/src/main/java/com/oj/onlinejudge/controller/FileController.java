package com.oj.onlinejudge.controller;

// 文件上传控制器：仅提供头像上传

import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.service.AvatarStorageService;
import com.oj.onlinejudge.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@Tag(name = "文件", description = "文件上传接口")
@RequiredArgsConstructor
public class FileController {

    private final AvatarStorageService avatarStorageService;

    @Operation(summary = "上传头像", description = "仅支持正方形图片（png/jpg/jpeg/gif/webp），返回可访问URL")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, String>> uploadAvatar(
            @Parameter(description = "当前认证用户") @RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false) AuthenticatedUser current,
            @Parameter(description = "头像文件") @org.springframework.web.bind.annotation.RequestPart("file") MultipartFile file) throws IOException {
        if (current == null) {
            throw ApiException.unauthorized("未登录或Token失效");
        }
        String url = avatarStorageService.storeAvatar(file);
        return ApiResponse.success(Map.of("url", url));
    }
}
