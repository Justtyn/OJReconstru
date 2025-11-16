package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.oj.onlinejudge.domain.entity.Admin;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("分页查询管理员列表")
    void listAdmins() throws Exception {
        String tk = registerStudent().token();
        authed(get("/api/admins").param("page", "1").param("size", "5"), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("查询管理员详情-存在")
    void getAdmin_found() throws Exception {
        String tk = registerStudent().token();
        JsonNode created = createAdmin(tk);
        authed(get("/api/admins/{id}", created.get("id").asLong()), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.username", is(created.get("username").asText())));
    }

    @Test
    @DisplayName("查询管理员详情-不存在")
    void getAdmin_notFound() throws Exception {
        String tk = registerStudent().token();
        authed(get("/api/admins/{id}", 9999), tk)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    @DisplayName("创建管理员")
    void createAdmin() throws Exception {
        String tk = registerStudent().token();
        JsonNode created = createAdmin(tk);
        Assertions.assertThat(created.get("id").asLong()).isPositive();
    }

    @Test
    @DisplayName("更新管理员-存在")
    void updateAdmin_found() throws Exception {
        String tk = registerStudent().token();
        JsonNode created = createAdmin(tk);
        Admin body = new Admin();
        body.setName("Alice-Updated");
        authed(put("/api/admins/{id}", created.get("id").asLong())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.name", is("Alice-Updated")));
    }

    @Test
    @DisplayName("更新管理员-不存在")
    void updateAdmin_notFound() throws Exception {
        String tk = registerStudent().token();
        Admin body = new Admin();
        body.setName("Nobody");
        authed(put("/api/admins/{id}", 9999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)), tk)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    @DisplayName("删除管理员-存在")
    void deleteAdmin_found() throws Exception {
        String tk = registerStudent().token();
        JsonNode created = createAdmin(tk);
        authed(delete("/api/admins/{id}", created.get("id").asLong()), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }

    @Test
    @DisplayName("删除管理员-不存在")
    void deleteAdmin_notFound() throws Exception {
        String tk = registerStudent().token();
        authed(delete("/api/admins/{id}", 9999), tk)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(404)));
    }

    private JsonNode createAdmin(String token) throws Exception {
        Admin body = new Admin();
        body.setUsername(uniqueUsername("admin"));
        body.setPassword("pwd12345");
        body.setName("Bob");
        return readJson(authed(post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)), token)).get("data");
    }
}
