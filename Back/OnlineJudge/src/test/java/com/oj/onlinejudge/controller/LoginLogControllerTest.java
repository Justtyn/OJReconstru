package com.oj.onlinejudge.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginLogControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("登录日志-分页列表 with filters")
    void listLogs() throws Exception {
        ControllerTestSupport.TestUser user = registerStudent();
        authed(get("/api/login-logs")
                .param("page", "1")
                .param("size", "5")
                .param("role", "student")
                .param("userId", user.id().toString()), user.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records[0].role", is("student")));
    }

    @Test
    @DisplayName("登录日志-详情不存在")
    void get_notFound() throws Exception {
        String tk = registerStudent().token();
        authed(get("/api/login-logs/{id}", 9999), tk)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(404)));
    }
}
