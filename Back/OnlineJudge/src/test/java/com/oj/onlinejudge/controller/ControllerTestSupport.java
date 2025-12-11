package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.domain.dto.LoginRequest;
import com.oj.onlinejudge.domain.dto.RegisterRequest;
import com.oj.onlinejudge.domain.dto.TeacherUpsertRequest;
import com.oj.onlinejudge.domain.entity.Admin;
import com.oj.onlinejudge.domain.vo.AuthUserVO;
import com.oj.onlinejudge.security.PasswordService;
import com.oj.onlinejudge.service.AdminService;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * 端到端集成测试统一基类。
 *
 * <p>1. 通过真实接口注册/登录用户，返回 {@link AuthSession}，避免伪造上下文。 2. 提供常用 HTTP、断言、
 * Mock 文件等工具，降低样板代码。 3. 默认使用 test profile + 事务，保证数据隔离。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
abstract class ControllerTestSupport {

    private static final AtomicInteger UNIQUE = new AtomicInteger();

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected AdminService adminService;
    @Autowired protected PasswordService passwordService;

    /* --------------------- 唯一值/密码生成 --------------------- */
    protected String uniqueSuffix() {
        return UNIQUE.incrementAndGet() + "-" + System.nanoTime();
    }

    protected String uniqueUsername(String prefix) {
        return prefix + "-" + uniqueSuffix();
    }

    protected String uniqueEmail(String prefix) {
        return prefix + "+" + uniqueSuffix() + "@example.com";
    }

    protected String strongPassword(String prefix) {
        return prefix + "P@" + uniqueSuffix();
    }

    /* --------------------- 注册 / 登录 --------------------- */
    protected AuthSession registerAndLoginStudent() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(uniqueUsername("student"));
        request.setPassword(strongPassword("Stu"));
        request.setEmail(uniqueEmail("student"));
        request.setName("Student-" + request.getUsername());
        JsonNode root = assertOkAndStandardApiResponse(performPostJson("/api/auth/register", request, null));
        AuthUserVO user = objectMapper.treeToValue(root.get("data"), AuthUserVO.class);
        return new AuthSession(user, request.getPassword());
    }

    protected AuthSession registerAndLoginTeacher() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        TeacherUpsertRequest request = new TeacherUpsertRequest();
        request.setUsername(uniqueUsername("teacher"));
        request.setPassword(strongPassword("Teach"));
        request.setName("Teacher-" + request.getUsername());
        request.setEmail(uniqueEmail("teacher"));
        JsonNode created =
                assertOkAndStandardApiResponse(performPostJson("/api/teachers", request, admin.token())).get("data");
        assertThat(created.get("id")).isNotNull();
        return loginSession(request.getUsername(), request.getPassword(), "teacher");
    }

    protected AuthSession registerAndLoginAdmin() throws Exception {
        String username = uniqueUsername("admin");
        String password = strongPassword("Adm");
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordService.encode(password));
        admin.setName("Admin-" + username);
        admin.setSex("unknown");
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        adminService.save(admin);
        return loginSession(username, password, "admin");
    }

    /* --------------------- 兼容旧测试的辅助方法 --------------------- */
    @Deprecated
    protected TestUser registerStudent() throws Exception {
        return new TestUser(registerAndLoginStudent());
    }

    @Deprecated
    protected TestUser registerStudent(String username, String password) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(uniqueEmail(username));
        request.setName("Student-" + username);
        return new TestUser(registerAndLoginStudent(username, password, request.getEmail()));
    }

    @Deprecated
    protected TestUser registerStudent(String username, String password, String email) throws Exception {
        return new TestUser(registerAndLoginStudent(username, password, email));
    }

    private AuthSession registerAndLoginStudent(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);
        request.setName("Student-" + username);
        JsonNode root = assertOkAndStandardApiResponse(performPostJson("/api/auth/register", request, null));
        AuthUserVO user = objectMapper.treeToValue(root.get("data"), AuthUserVO.class);
        return new AuthSession(user, password);
    }

    protected TestUser createTeacherUser() throws Exception {
        return new TestUser(registerAndLoginTeacher());
    }

    protected TestUser createAdminUser() throws Exception {
        return new TestUser(registerAndLoginAdmin());
    }

    protected String login(String username, String password) throws Exception {
        return loginSession(username, password, null).token();
    }

    protected String login(String username, String password, String role) throws Exception {
        return loginSession(username, password, role).token();
    }

    protected AuthSession loginSession(String username, String password) throws Exception {
        return loginSession(username, password, null);
    }

    protected AuthSession loginSession(String username, String password, String role) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        if (role != null) {
            request.setRole(role);
        }
        ResultActions action = performPostJson("/api/auth/login", request, null).andExpect(status().isOk());
        JsonNode root = readBody(action);
        AuthUserVO user = objectMapper.treeToValue(root.get("data"), AuthUserVO.class);
        return new AuthSession(user, password);
    }

    /* --------------------- HTTP 工具 --------------------- */
    protected ResultActions performPostJson(String url, Object body, String bearerToken) throws Exception {
        byte[] payload = body == null ? "{}".getBytes(java.nio.charset.StandardCharsets.UTF_8)
                : objectMapper.writeValueAsBytes(body);
        MockHttpServletRequestBuilder builder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .accept(MediaType.APPLICATION_JSON);
        return perform(builder, bearerToken);
    }

    protected ResultActions performPutJson(String url, Object body, String bearerToken) throws Exception {
        byte[] payload = body == null ? "{}".getBytes(java.nio.charset.StandardCharsets.UTF_8)
                : objectMapper.writeValueAsBytes(body);
        MockHttpServletRequestBuilder builder = put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .accept(MediaType.APPLICATION_JSON);
        return perform(builder, bearerToken);
    }

    protected ResultActions performGet(String url, MultiValueMap<String, String> params, String bearerToken)
            throws Exception {
        MockHttpServletRequestBuilder builder = get(url).accept(MediaType.APPLICATION_JSON);
        if (params != null) {
            builder.params(params);
        }
        return perform(builder, bearerToken);
    }

    protected ResultActions performDelete(String url, MultiValueMap<String, String> params, String bearerToken)
            throws Exception {
        MockHttpServletRequestBuilder builder = delete(url).accept(MediaType.APPLICATION_JSON);
        if (params != null) {
            builder.params(params);
        }
        return perform(builder, bearerToken);
    }

    protected ResultActions performMultipart(
            String url, List<MockMultipartFile> files, Map<String, String> formFields, String bearerToken)
            throws Exception {
        org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder builder = multipart(url);
        if (files != null) {
            for (MockMultipartFile file : files) {
                builder.file(file);
            }
        }
        if (formFields != null) {
            formFields.forEach(builder::param);
        }
        builder.accept(MediaType.APPLICATION_JSON);
        return perform(builder, bearerToken);
    }

    private ResultActions perform(MockHttpServletRequestBuilder builder, String bearerToken) throws Exception {
        if (StringUtils.hasText(bearerToken)) {
            builder.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }
        return mockMvc.perform(builder);
    }

    /* --------------------- 断言/JSON 工具 --------------------- */
    protected JsonNode assertOkAndStandardApiResponse(ResultActions actions) throws Exception {
        return assertStatusAndReturn(actions, status().isOk(), 0);
    }

    protected JsonNode assertBadRequestAndValidationErrors(ResultActions actions) throws Exception {
        JsonNode root = assertStatusAndReturn(actions, status().isBadRequest(), 400);
        assertThat(root.get("message").asText()).isNotEmpty();
        return root;
    }

    protected JsonNode assertForbidden(ResultActions actions) throws Exception {
        return assertStatusAndReturn(actions, status().isForbidden(), 403);
    }

    protected JsonNode assertUnauthorized(ResultActions actions) throws Exception {
        return assertStatusAndReturn(actions, status().isUnauthorized(), 401);
    }

    private JsonNode assertStatusAndReturn(ResultActions actions, ResultMatcher matcher, int expectedCode)
            throws Exception {
        actions.andExpect(matcher);
        JsonNode root = readBody(actions);
        assertThat(root.has("code")).isTrue();
        assertThat(root.has("message")).isTrue();
        if (expectedCode >= 0) {
            assertThat(root.get("code").asInt()).isEqualTo(expectedCode == 0 ? 0 : expectedCode);
        }
        return root;
    }

    protected JsonNode readBody(ResultActions actions) throws Exception {
        return objectMapper.readTree(actions.andReturn().getResponse().getContentAsString());
    }

    protected <T> T readData(ResultActions actions, Class<T> type) throws Exception {
        JsonNode root = assertOkAndStandardApiResponse(actions);
        return objectMapper.treeToValue(root.get("data"), type);
    }

    protected <T> T readData(ResultActions actions, TypeReference<T> type) throws Exception {
        JsonNode root = assertOkAndStandardApiResponse(actions);
        return objectMapper.readValue(root.get("data").traverse(), type);
    }

    /* --------------------- 文件辅助 --------------------- */
    protected MockMultipartFile squarePng(String fieldName, String filename, int size) {
        return new MockMultipartFile(fieldName, filename, MediaType.IMAGE_PNG_VALUE, generatePng(size, size));
    }

    protected MockMultipartFile rectangularPng(String fieldName, String filename, int width, int height) {
        return new MockMultipartFile(fieldName, filename, MediaType.IMAGE_PNG_VALUE, generatePng(width, height));
    }

    protected MockMultipartFile textFile(String fieldName, String filename, String content) {
        return new MockMultipartFile(fieldName, filename, MediaType.TEXT_PLAIN_VALUE, content.getBytes());
    }

    private byte[] generatePng(int width, int height) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(0, 0, width - 1, height - 1);
            graphics.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create mock image", e);
        }
    }

    /* --------------------- 参数构造 --------------------- */
    protected MultiValueMap<String, String> params(String key, String value) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(key, value);
        return params;
    }

    protected MultiValueMap<String, String> params(Map<String, String> source) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (source != null) {
            source.forEach(params::add);
        }
        return params;
    }

    /* --------------------- 旧测试兼容辅助 --------------------- */
    protected ResultActions authed(MockHttpServletRequestBuilder builder, String bearerToken) throws Exception {
        builder.accept(MediaType.APPLICATION_JSON);
        return perform(builder, bearerToken);
    }

    protected JsonNode readJson(ResultActions actions) throws Exception {
        return readBody(actions);
    }

    protected String uniqueLabel(String prefix) {
        return prefix + "-" + uniqueSuffix();
    }

    /* --------------------- 会话封装 --------------------- */
    protected static final class AuthSession {
        private final AuthUserVO user;
        private final String rawPassword;

        private AuthSession(AuthUserVO user, String rawPassword) {
            this.user = user;
            this.rawPassword = rawPassword;
        }

        public AuthUserVO user() {
            return user;
        }

        public String rawPassword() {
            return rawPassword;
        }

        public String token() {
            return user.getToken();
        }

        public Long id() {
            return user.getId();
        }

        public String username() {
            return user.getUsername();
        }

        public String role() {
            return user.getRole();
        }
    }

    /**
     * 旧测试仍依赖的结构体。后续新测试请改用 {@link AuthSession}。
     */
    @Deprecated
    protected static final class TestUser {
        private final AuthSession delegate;

        private TestUser(AuthSession delegate) {
            this.delegate = delegate;
        }

        public Long id() {
            return delegate.id();
        }

        public String username() {
            return delegate.username();
        }

        public String rawPassword() {
            return delegate.rawPassword();
        }

        public String token() {
            return delegate.token();
        }

        public String role() {
            return delegate.role();
        }

        public String email() {
            return delegate.user().getEmail();
        }
    }
}
