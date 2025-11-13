package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.domain.entity.ClassesMember;
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
class ClassesMemberControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String token() throws Exception {
        String u = "cm-t-" + System.currentTimeMillis();
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
    @DisplayName("班级成员-分页列表 with filter")
    void listMembers() throws Exception {
        String tk = token();
        mockMvc.perform(get("/api/classes-members").param("page","1").param("size","5").param("classId","1").header("Authorization", tk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records[0].classId", is(1)));
    }

    @Test
    @DisplayName("班级成员-CRUD")
    void crudMember() throws Exception {
        String tk = token();
        ClassesMember cm = new ClassesMember();
        cm.setClassId(1L); cm.setMemberType("student"); cm.setStudentId(1L);
        String body = objectMapper.writeValueAsString(cm);
        String res = mockMvc.perform(post("/api/classes-members")
                .header("Authorization", tk)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(res).get("data").get("id").asLong();

        ClassesMember patch = new ClassesMember(); patch.setMemberType("teacher");
        String upd = objectMapper.writeValueAsString(patch);
        mockMvc.perform(put("/api/classes-members/{id}", id)
                .header("Authorization", tk)
                .contentType(MediaType.APPLICATION_JSON)
                .content(upd))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.memberType", is("teacher")));

        mockMvc.perform(delete("/api/classes-members/{id}", id).header("Authorization", tk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }
}
