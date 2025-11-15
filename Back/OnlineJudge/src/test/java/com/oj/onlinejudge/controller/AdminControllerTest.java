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

import java.util.HashMap;
import java.util.Map;

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

    private String token() throws Exception {
        String u = "admin-t-" + System.currentTimeMillis();
        Map<String, Object> rr = new HashMap<>();
        rr.put("username", u);
        rr.put("password", "pwd123");
        rr.put("email", u + "@example.com");
        rr.put("name", "Test");
        String res = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rr)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(res).get("data").get("token").asText();
    }

    @Test
    @DisplayName("分页查询管理员列表")
    void listAdmins() throws Exception {
        String tk = token();
        mockMvc.perform(get("/api/admins").param("page","1").param("size","5").header("Authorization", tk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("查询管理员详情-存在")
    void getAdmin_found() throws Exception {
        String tk = token();
        mockMvc.perform(get("/api/admins/{id}", 1).header("Authorization", tk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    @DisplayName("查询管理员详情-不存在")
    void getAdmin_notFound() throws Exception {
        String tk = token();
        mockMvc.perform(get("/api/admins/{id}", 9999).header("Authorization", tk))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    @DisplayName("创建管理员")
    void createAdmin() throws Exception {
        String tk = token();
        Admin body = new Admin();
        body.setUsername("bob");
        body.setPassword("pwd");
        body.setName("Bob");
        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(post("/api/admins")
                .header("Authorization", tk)
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
        String tk = token();
        Admin body = new Admin();
        body.setName("Alice-Updated");
        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/api/admins/{id}", 2)
                .header("Authorization", tk)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.name", is("Alice-Updated")));
    }

    @Test
    @DisplayName("更新管理员-不存在")
    void updateAdmin_notFound() throws Exception {
        String tk = token();
        Admin body = new Admin();
        body.setName("Nobody");
        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/api/admins/{id}", 9999)
                .header("Authorization", tk)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    @DisplayName("删除管理员-存在")
    void deleteAdmin_found() throws Exception {
        String tk = token();
        mockMvc.perform(delete("/api/admins/{id}", 1).header("Authorization", tk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }

    @Test
    @DisplayName("删除管理员-不存在")
    void deleteAdmin_notFound() throws Exception {
        String tk = token();
        mockMvc.perform(delete("/api/admins/{id}", 9999).header("Authorization", tk))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(404)));
    }
}
