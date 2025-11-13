package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.domain.entity.Admin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("分页查询管理员列表")
    void listAdmins() throws Exception {
        mockMvc.perform(get("/api/admins").param("page","1").param("size","5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("查询管理员详情-存在")
    void getAdmin_found() throws Exception {
        mockMvc.perform(get("/api/admins/{id}", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    @DisplayName("查询管理员详情-不存在")
    void getAdmin_notFound() throws Exception {
        mockMvc.perform(get("/api/admins/{id}", 9999))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    @DisplayName("创建管理员")
    void createAdmin() throws Exception {
        Admin body = new Admin();
        body.setUsername("bob");
        body.setPassword("pwd");
        body.setName("Bob");
        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.id", notNullValue()))
            .andExpect(jsonPath("$.data.username", is("bob")));
    }

    @Test
    @DisplayName("更新管理员-存在")
    void updateAdmin_found() throws Exception {
        Admin body = new Admin();
        body.setName("Alice-Updated");
        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/api/admins/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.name", is("Alice-Updated")));
    }

    @Test
    @DisplayName("更新管理员-不存在")
    void updateAdmin_notFound() throws Exception {
        Admin body = new Admin();
        body.setName("Nobody");
        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/api/admins/{id}", 9999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    @DisplayName("删除管理员-存在")
    void deleteAdmin_found() throws Exception {
        mockMvc.perform(delete("/api/admins/{id}", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }

    @Test
    @DisplayName("删除管理员-不存在")
    void deleteAdmin_notFound() throws Exception {
        mockMvc.perform(delete("/api/admins/{id}", 9999))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(404)));
    }
}

