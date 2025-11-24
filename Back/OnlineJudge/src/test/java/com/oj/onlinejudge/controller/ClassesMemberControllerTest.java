package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.domain.entity.ClassesMember;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClassesMemberControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("班级成员-分页列表 with filter")
    void listMembers() throws Exception {
        TestUser teacher = createTeacherUser();
        JsonNode classNode = createClass(teacher.token());
        TestUser student = registerStudent();
        createMember(teacher.token(), classNode.get("id").asLong(), student.id());

        JsonNode response = readJson(authed(get("/api/classes-members")
                .param("page", "1")
                .param("size", "5")
                .param("classId", classNode.get("id").asText()), teacher.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0))));
        JsonNode records = response.get("data").get("records");
        Assertions.assertThat(records.size()).isGreaterThan(0);
        Assertions.assertThat(records.get(0).get("classId").asLong()).isEqualTo(classNode.get("id").asLong());
    }

    @Test
    @DisplayName("班级成员-CRUD")
    void crudMember() throws Exception {
        TestUser teacher = createTeacherUser();
        JsonNode classNode = createClass(teacher.token());
        TestUser student = registerStudent();

        JsonNode created = createMember(teacher.token(), classNode.get("id").asLong(), student.id());

        ClassesMember patch = new ClassesMember();
        patch.setMemberType("teacher");
        authed(put("/api/classes-members/{id}", created.get("id").asLong())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patch)), teacher.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.memberType", is("teacher")));

        authed(delete("/api/classes-members/{id}", created.get("id").asLong()), teacher.token())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }

    private JsonNode createClass(String token) throws Exception {
        Classes c = new Classes();
        c.setName("Clazz-" + uniqueLabel("member"));
        c.setCode("CODE-" + uniqueLabel("member"));
        return readJson(authed(post("/api/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)), token)).get("data");
    }

    private JsonNode createMember(String token, long classId, long studentId) throws Exception {
        ClassesMember cm = new ClassesMember();
        cm.setClassId(classId);
        cm.setMemberType("student");
        cm.setStudentId(studentId);
        return readJson(authed(post("/api/classes-members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cm)), token)).get("data");
    }
}
