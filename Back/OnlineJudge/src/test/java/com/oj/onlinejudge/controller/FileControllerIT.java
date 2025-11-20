package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

@DisplayName("FileController 集成测试")
class FileControllerIT extends ControllerTestSupport {

    @Value("${app.storage.avatar-dir}")
    private String avatarDir;

    @Value("${app.storage.avatar-url-prefix}")
    private String avatarUrlPrefix;

    @Test
    @DisplayName("上传正方形头像成功并返回正确 URL")
    void uploadAvatar_success() throws Exception {
        AuthSession user = registerAndLoginStudent();
        MockMultipartFile file = squarePng("file", "avatar.png", 80);
        Map<String, String> data = readData(
                performMultipart("/api/files/avatar", java.util.List.of(file), null, user.token()),
                new TypeReference<Map<String, String>>() {});
        String url = data.get("url");
        assertThat(url).startsWith(avatarUrlPrefix);
        String filename = url.substring(url.lastIndexOf('/') + 1);
        assertThat(Files.exists(Path.of(avatarDir, filename))).isTrue();
    }

    @Test
    @DisplayName("未登录上传返回 401")
    void uploadAvatar_unauthorized() throws Exception {
        MockMultipartFile file = squarePng("file", "avatar.png", 64);
        assertUnauthorized(performMultipart("/api/files/avatar", java.util.List.of(file), null, null));
    }

    @Test
    @DisplayName("非正方形图片或非法格式返回 400")
    void uploadAvatar_invalid() throws Exception {
        AuthSession user = registerAndLoginStudent();
        MockMultipartFile rectangle = rectangularPng("file", "rect.png", 80, 60);
        assertBadRequestAndValidationErrors(
                performMultipart("/api/files/avatar", java.util.List.of(rectangle), null, user.token()));

        MockMultipartFile text = textFile("file", "note.txt", "not image");
        assertBadRequestAndValidationErrors(
                performMultipart("/api/files/avatar", java.util.List.of(text), null, user.token()));
    }
}
