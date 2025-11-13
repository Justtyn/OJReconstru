package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.domain.dto.LoginRequest;
import com.oj.onlinejudge.domain.dto.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @DisplayName("登录-用户名不存在")
    void login_notFound() throws Exception {
        LoginRequest lr = new LoginRequest(); lr.setUsername("nope"); lr.setPassword("x");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lr)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("登录-密码不匹配")
    void login_wrongPassword() throws Exception {
        // 先注册用户，再用错误密码尝试登录
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername("s1"); rr.setPassword("rightpass");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isOk());

        LoginRequest lr = new LoginRequest(); lr.setUsername("s1"); lr.setPassword("wrong");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lr)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("注册-成功并返回Token")
    void register_success() throws Exception {
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername("reg1"); rr.setPassword("123456"); rr.setEmail("r1@example.com"); rr.setName("Reg One");
        String res = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.token", startsWith("Bearer ")))
            .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(res).get("data");
        assertThat(node.get("username").asText()).isEqualTo("reg1");
    }

    @Test
    @DisplayName("注册-重复用户名")
    void register_duplicate() throws Exception {
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername("dup"); rr.setPassword("123456");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isOk()); // 第一次成功
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isBadRequest()); // 第二次非法参数
    }

    @Test
    @DisplayName("登录-成功与注销")
    void login_and_logout() throws Exception {
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername("lg1"); rr.setPassword("abcdef");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isOk());
        LoginRequest lr = new LoginRequest(); lr.setUsername("lg1"); lr.setPassword("abcdef");
        String loginRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lr)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.token", startsWith("Bearer ")))
            .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(loginRes).get("data").get("token").asText();
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }
}
