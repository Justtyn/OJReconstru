package com.oj.onlinejudge.controller;

import com.oj.onlinejudge.domain.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("学生-分页模糊搜索")
    void listStudents() throws Exception {
        String tk = registerStudent().token();
        authed(get("/api/students").param("page", "1").param("size", "5").param("username", "stu"), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("学生-CRUD（含密码哈希路径）")
    void crudStudent() throws Exception {
        String tk = registerStudent().token();
        Student s = new Student();
        s.setUsername(uniqueUsername("student-crud"));
        s.setPassword("pwd12345");
        s.setEmail(uniqueEmail("student-crud"));
        String body = objectMapper.writeValueAsString(s);

        String res = authed(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(body), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(res).get("data").get("id").asLong();

        Student patch = new Student();
        patch.setName("Stu2");
        String upd = objectMapper.writeValueAsString(patch);
        authed(put("/api/students/{id}", id).contentType(MediaType.APPLICATION_JSON).content(upd), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.name", is("Stu2")));

        authed(delete("/api/students/{id}", id), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }
}
