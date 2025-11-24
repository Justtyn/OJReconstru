package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.oj.onlinejudge.domain.dto.LoginRequest;
import com.oj.onlinejudge.domain.dto.RegisterRequest;
import com.oj.onlinejudge.domain.entity.Admin;
import com.oj.onlinejudge.domain.entity.Teacher;
import com.oj.onlinejudge.security.PasswordService;
import com.oj.onlinejudge.service.AdminService;
import com.oj.onlinejudge.service.TeacherService;
import com.oj.onlinejudge.service.VerificationCodeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends ControllerTestSupport {

    @Autowired TeacherService teacherService;
    @Autowired AdminService adminService;
    @Autowired PasswordService passwordService;
    @Autowired VerificationCodeService verificationCodeService;

    @Test
    @DisplayName("登录-用户名不存在")
    void login_notFound() throws Exception {
        LoginRequest lr = new LoginRequest();
        lr.setUsername("nope-" + uniqueLabel("login"));
        lr.setPassword("x");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lr)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("登录-密码不匹配")
    void login_wrongPassword() throws Exception {
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername(uniqueUsername("student"));
        rr.setPassword("rightpass");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isOk());

        LoginRequest lr = new LoginRequest();
        lr.setUsername(rr.getUsername());
        lr.setPassword("wrong");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lr)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("注册-成功并返回Token")
    void register_success() throws Exception {
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername(uniqueUsername("reg"));
        rr.setPassword("123456");
        rr.setEmail(uniqueEmail("reg"));
        rr.setName("Reg One");
        JsonNode node = readJson(mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.token", startsWith("Bearer "))));
        Assertions.assertThat(node.get("data").get("username").asText()).isEqualTo(rr.getUsername());
        Assertions.assertThat(node.get("data").get("role").asText()).isEqualTo("student");
    }

    @Test
    @DisplayName("注册-重复用户名")
    void register_duplicate() throws Exception {
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername(uniqueUsername("dup"));
        rr.setPassword("123456");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("登录-成功与注销")
    void login_and_logout() throws Exception {
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername(uniqueUsername("lg"));
        rr.setPassword("abcdef");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isOk());
        LoginRequest lr = new LoginRequest();
        lr.setUsername(rr.getUsername());
        lr.setPassword("abcdef");
        String loginRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lr)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.token", startsWith("Bearer ")))
            .andExpect(jsonPath("$.data.role", is("student")))
            .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(loginRes).get("data").get("token").asText();
        mockMvc.perform(post("/api/auth/logout").header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }

    @Test
    @DisplayName("教师登录-成功")
    void teacher_login_success() throws Exception {
        Teacher t = new Teacher();
        t.setUsername(uniqueUsername("teacher"));
        t.setPassword(passwordService.encode("teachpass"));
        teacherService.save(t);
        LoginRequest lr = new LoginRequest();
        lr.setUsername(t.getUsername());
        lr.setPassword("teachpass");
        lr.setRole("teacher");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lr)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.role", is("teacher")))
            .andExpect(jsonPath("$.data.token", startsWith("Bearer ")));
    }

    @Test
    @DisplayName("管理员登录-成功")
    void admin_login_success() throws Exception {
        Admin a = new Admin();
        a.setUsername(uniqueUsername("admin"));
        a.setPassword(passwordService.encode("adminpass"));
        adminService.save(a);
        LoginRequest lr = new LoginRequest();
        lr.setUsername(a.getUsername());
        lr.setPassword("adminpass");
        lr.setRole("admin");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lr)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.role", is("admin")))
            .andExpect(jsonPath("$.data.token", startsWith("Bearer ")));
    }

    @Test
    @DisplayName("当前用户信息-成功")
    void me_success() throws Exception {
        ControllerTestSupport.TestUser user = registerStudent();
        authed(get("/api/auth/users/me"), user.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.username", is(user.username())))
            .andExpect(jsonPath("$.data.role", is("student")))
            .andExpect(jsonPath("$.data.details.password").doesNotExist());
    }

    @Test
    @DisplayName("当前用户信息-未登录")
    void me_unauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/users/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code", is(401)));
    }

    @Test
    @DisplayName("修改密码-成功")
    void changePassword_success() throws Exception {
        ControllerTestSupport.TestUser user = registerStudent();
        String newPassword = "NewPwd123!";
        com.fasterxml.jackson.databind.node.ObjectNode req = objectMapper.createObjectNode();
        req.put("oldPassword", user.rawPassword());
        req.put("newPassword", newPassword);

        authed(post("/api/auth/password/change")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(req)), user.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));

        // new password works
        String token = login(user.username(), newPassword);
        Assertions.assertThat(token).startsWith("Bearer ");
    }

    @Test
    @DisplayName("修改密码-旧密码错误")
    void changePassword_wrongOld() throws Exception {
        ControllerTestSupport.TestUser user = registerStudent();
        com.fasterxml.jackson.databind.node.ObjectNode req = objectMapper.createObjectNode();
        req.put("oldPassword", "bad");
        req.put("newPassword", "Pw123456");
        authed(post("/api/auth/password/change")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(req)), user.token())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is(400)));
    }

    @Test
    @DisplayName("找回密码-发送与验证成功")
    void forgotPassword_flow() throws Exception {
        ControllerTestSupport.TestUser user = registerStudent(uniqueUsername("fp"), "InitPwd123", uniqueEmail("fp"));
        com.fasterxml.jackson.databind.node.ObjectNode sendReq = objectMapper.createObjectNode();
        sendReq.put("username", user.username());
        mockMvc.perform(post("/api/auth/password/forgot/sendCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(sendReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));

        VerificationCodeService.Pending pending = verificationCodeService.get("pwd:" + user.username())
                .orElseThrow(() -> new IllegalStateException("code missing"));

        com.fasterxml.jackson.databind.node.ObjectNode verifyReq = objectMapper.createObjectNode();
        verifyReq.put("username", user.username());
        verifyReq.put("code", pending.code);
        verifyReq.put("newPassword", "ResetPwd789");
        mockMvc.perform(post("/api/auth/password/forgot/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(verifyReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));

        String token = login(user.username(), "ResetPwd789");
        Assertions.assertThat(token).startsWith("Bearer ");
    }
}
