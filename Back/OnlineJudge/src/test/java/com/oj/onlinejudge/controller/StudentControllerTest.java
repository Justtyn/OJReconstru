package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.domain.entity.Student;
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
class StudentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @DisplayName("学生-分页模糊搜索")
    void listStudents() throws Exception {
        mockMvc.perform(get("/api/students").param("page","1").param("size","5").param("username","s"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("学生-CRUD（含密码哈希路径）")
    void crudStudent() throws Exception {
        Student s = new Student(); s.setUsername("s2"); s.setPassword("pwd"); s.setEmail("s2@example.com");
        String body = objectMapper.writeValueAsString(s);
        String res = mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(res).get("data").get("id").asLong();

        Student patch = new Student(); patch.setName("Stu2");
        String upd = objectMapper.writeValueAsString(patch);
        mockMvc.perform(put("/api/students/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(upd))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.name", is("Stu2")));

        mockMvc.perform(delete("/api/students/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }
}

