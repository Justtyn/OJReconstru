package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.domain.entity.Teacher;
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
class TeacherControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String token() throws Exception {
        String u = "teacher-t-" + System.currentTimeMillis();
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
    @DisplayName("教师-分页列表")
    void listTeachers() throws Exception {
        String tk = token();
        mockMvc.perform(get("/api/teachers").param("page","1").param("size","5").header("Authorization", tk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("教师-详情存在")
    void getTeacher_found() throws Exception {
        String tk = token();
        mockMvc.perform(get("/api/teachers/{id}", 1).header("Authorization", tk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    @DisplayName("教师-创建/更新/删除")
    void crudTeacher() throws Exception {
        String tk = token();
        Teacher t = new Teacher();
        t.setUsername("t2"); t.setPassword("pwd"); t.setName("Teacher Two");
        String createJson = objectMapper.writeValueAsString(t);

        String res = mockMvc.perform(post("/api/teachers")
                .header("Authorization", tk)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.id", notNullValue()))
            .andReturn().getResponse().getContentAsString();

        Teacher created = objectMapper.readValue(res, com.fasterxml.jackson.databind.JsonNode.class)
            .get("data").traverse(objectMapper).readValueAs(Teacher.class);

        Teacher patch = new Teacher(); patch.setName("T2-Updated");
        String updateJson = objectMapper.writeValueAsString(patch);
        mockMvc.perform(put("/api/teachers/{id}", created.getId())
                .header("Authorization", tk)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.name", is("T2-Updated")));

        mockMvc.perform(delete("/api/teachers/{id}", created.getId()).header("Authorization", tk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }
}
