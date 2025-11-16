package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.oj.onlinejudge.domain.dto.StudentJoinClassRequest;
import com.oj.onlinejudge.domain.entity.Classes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentClassControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("学生加入班级成功并可查询")
    void joinAndFetch() throws Exception {
        TestUser teacher = createTeacherUser();
        JsonNode clazz = createClass(teacher.token());

        TestUser student = registerStudent();
        StudentJoinClassRequest req = new StudentJoinClassRequest();
        req.setCode(clazz.get("code").asText());

        authed(post("/api/student/classes/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)), student.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.id").value(clazz.get("id").asLong()));

        authed(get("/api/student/classes"), student.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(clazz.get("id").asLong()));
    }

    @Test
    @DisplayName("学生重复加入不同班级返回冲突")
    void joinConflict() throws Exception {
        TestUser teacher = createTeacherUser();
        JsonNode classOne = createClass(teacher.token());
        JsonNode classTwo = createClass(teacher.token());

        TestUser student = registerStudent();
        StudentJoinClassRequest req1 = new StudentJoinClassRequest();
        req1.setCode(classOne.get("code").asText());
        authed(post("/api/student/classes/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)), student.token())
            .andExpect(status().isOk());

        StudentJoinClassRequest req2 = new StudentJoinClassRequest();
        req2.setCode(classTwo.get("code").asText());
        authed(post("/api/student/classes/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)), student.token())
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code", is(409)));
    }

    @Test
    @DisplayName("学生退出班级后可重新加入")
    void leaveAndRejoin() throws Exception {
        TestUser teacher = createTeacherUser();
        JsonNode clazz = createClass(teacher.token());

        TestUser student = registerStudent();
        StudentJoinClassRequest req = new StudentJoinClassRequest();
        req.setCode(clazz.get("code").asText());
        authed(post("/api/student/classes/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)), student.token())
            .andExpect(status().isOk());

        authed(post("/api/student/classes/leave"), student.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));

        authed(post("/api/student/classes/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)), student.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }

    @Test
    @DisplayName("学生退出后可加入其他班级")
    void leaveAndJoinAnother() throws Exception {
        TestUser teacher = createTeacherUser();
        JsonNode classA = createClass(teacher.token());
        JsonNode classB = createClass(teacher.token());

        TestUser student = registerStudent();
        StudentJoinClassRequest reqA = new StudentJoinClassRequest();
        reqA.setCode(classA.get("code").asText());
        authed(post("/api/student/classes/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqA)), student.token())
            .andExpect(status().isOk());

        authed(post("/api/student/classes/leave"), student.token())
            .andExpect(status().isOk());

        StudentJoinClassRequest reqB = new StudentJoinClassRequest();
        reqB.setCode(classB.get("code").asText());
        authed(post("/api/student/classes/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqB)), student.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(classB.get("id").asLong()));
    }

    private JsonNode createClass(String token) throws Exception {
        Classes c = new Classes();
        c.setName("StuClass-" + uniqueLabel("clazz"));
        c.setCode("CODE-" + uniqueLabel("clazz"));
        return readJson(authed(post("/api/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)), token)).get("data");
    }
}
