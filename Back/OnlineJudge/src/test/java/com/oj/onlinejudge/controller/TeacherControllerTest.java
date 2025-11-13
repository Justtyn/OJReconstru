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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeacherControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @DisplayName("教师-分页列表")
    void listTeachers() throws Exception {
        mockMvc.perform(get("/api/teachers").param("page","1").param("size","5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("教师-详情存在")
    void getTeacher_found() throws Exception {
        mockMvc.perform(get("/api/teachers/{id}", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    @DisplayName("教师-创建/更新/删除")
    void crudTeacher() throws Exception {
        Teacher t = new Teacher();
        t.setUsername("t2"); t.setPassword("pwd"); t.setName("Teacher Two");
        String createJson = objectMapper.writeValueAsString(t);

        String res = mockMvc.perform(post("/api/teachers")
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.name", is("T2-Updated")));

        mockMvc.perform(delete("/api/teachers/{id}", created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }
}

