package com.oj.onlinejudge.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginLogControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    @DisplayName("登录日志-分页列表 with filters")
    void listLogs() throws Exception {
        mockMvc.perform(get("/api/login-logs").param("page","1").param("size","5").param("role","student").param("userId","1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records[0].role", is("student")));
    }

    @Test
    @DisplayName("登录日志-详情不存在")
    void get_notFound() throws Exception {
        mockMvc.perform(get("/api/login-logs/{id}", 9999))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(404)));
    }
}

