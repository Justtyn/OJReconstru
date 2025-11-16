package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.oj.onlinejudge.domain.entity.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TeacherControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("教师-分页列表")
    void listTeachers() throws Exception {
        String tk = registerStudent().token();
        authed(get("/api/teachers").param("page", "1").param("size", "5"), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("教师-详情存在")
    void getTeacher_found() throws Exception {
        String tk = registerStudent().token();
        Teacher t = new Teacher();
        t.setUsername(uniqueUsername("teacher"));
        t.setPassword("Pwd123!");
        t.setName("Teacher Detail");
        t.setEmail(uniqueEmail("teacher"));
        JsonNode created = readJson(authed(post("/api/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)), tk)).get("data");

        authed(get("/api/teachers/{id}", created.get("id").asLong()), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.username", is(created.get("username").asText())));
    }

    @Test
    @DisplayName("教师-创建/更新/删除")
    void crudTeacher() throws Exception {
        String tk = registerStudent().token();
        Teacher t = new Teacher();
        t.setUsername(uniqueUsername("teacher"));
        t.setPassword("pwd1234");
        t.setName("Teacher Two");
        String createJson = objectMapper.writeValueAsString(t);

        JsonNode created = readJson(authed(post("/api/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson), tk)).get("data");

        Teacher patch = new Teacher();
        patch.setName("T2-Updated");
        String updateJson = objectMapper.writeValueAsString(patch);
        authed(put("/api/teachers/{id}", created.get("id").asLong())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.name", is("T2-Updated")));

        authed(delete("/api/teachers/{id}", created.get("id").asLong()), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }
}
