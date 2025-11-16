package com.oj.onlinejudge.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.domain.entity.Teacher;
import com.oj.onlinejudge.security.PasswordService;
import com.oj.onlinejudge.service.TeacherService;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired protected TeacherService teacherService;
    @Autowired protected PasswordService passwordService;

    private static final AtomicInteger SEQ = new AtomicInteger();

    protected TestUser registerStudent() throws Exception {
        return registerStudent(uniqueUsername("student"), "Pwd123!", uniqueEmail("student"));
    }

    protected TestUser registerStudent(String username, String password) throws Exception {
        return registerStudent(username, password, uniqueEmail(username));
    }

    protected TestUser registerStudent(String username, String password, String email) throws Exception {
        com.fasterxml.jackson.databind.node.ObjectNode req = objectMapper.createObjectNode();
        req.put("username", username);
        req.put("password", password);
        req.put("name", "Tester-" + username);
        if (email != null) {
            req.put("email", email);
        }
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        Assertions.assertThat(response.getStatus())
                .withFailMessage("register failed: %s", content)
                .isEqualTo(200);
        JsonNode data = objectMapper.readTree(content).get("data");
        return new TestUser(
                data.get("id").asLong(),
                username,
                password,
                data.get("token").asText(),
                data.get("role").asText(),
                email);
    }

    protected TestUser createTeacherUser() throws Exception {
        String username = uniqueUsername("teacher");
        String rawPassword = "Pwd12345!";
        Teacher teacher = new Teacher();
        teacher.setUsername(username);
        teacher.setPassword(passwordService.encode(rawPassword));
        teacherService.save(teacher);
        String token = login(username, rawPassword, "teacher");
        return new TestUser(teacher.getId(), username, rawPassword, token, "teacher", teacher.getEmail());
    }

    protected String login(String username, String password) throws Exception {
        return login(username, password, null);
    }

    protected String login(String username, String password, String role) throws Exception {
        com.fasterxml.jackson.databind.node.ObjectNode req = objectMapper.createObjectNode();
        req.put("username", username);
        req.put("password", password);
        if (role != null) {
            req.put("role", role);
        }
        String res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(res).get("data").get("token").asText();
    }

    protected ResultActions authed(MockHttpServletRequestBuilder builder, String token) throws Exception {
        return mockMvc.perform(builder.header("Authorization", token));
    }

    protected JsonNode readJson(ResultActions actions) throws Exception {
        return objectMapper.readTree(actions.andReturn().getResponse().getContentAsString());
    }

    protected String uniqueUsername(String prefix) {
        return prefix + "-" + System.nanoTime() + "-" + SEQ.incrementAndGet();
    }

    protected String uniqueEmail(String prefix) {
        return prefix + "+" + SEQ.incrementAndGet() + "@example.com";
    }

    protected String uniqueLabel(String prefix) {
        return prefix + "-" + SEQ.incrementAndGet();
    }

    protected static final class TestUser {
        private final Long id;
        private final String username;
        private final String rawPassword;
        private final String token;
        private final String role;
        private final String email;

        private TestUser(Long id, String username, String rawPassword, String token, String role, String email) {
            this.id = id;
            this.username = username;
            this.rawPassword = rawPassword;
            this.token = token;
            this.role = role;
            this.email = email;
        }

        public Long id() {
            return id;
        }

        public String username() {
            return username;
        }

        public String rawPassword() {
            return rawPassword;
        }

        public String token() {
            return token;
        }

        public String role() {
            return role;
        }

        public String email() {
            return email;
        }
    }
}
