package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.domain.entity.Classes;
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
class ClassesControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @DisplayName("班级-分页列表")
    void listClasses() throws Exception {
        mockMvc.perform(get("/api/classes").param("page","1").param("size","5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("班级-详情不存在")
    void getClass_notFound() throws Exception {
        mockMvc.perform(get("/api/classes/{id}", 9999))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    @DisplayName("班级-创建/更新/删除")
    void crudClass() throws Exception {
        Classes c = new Classes(); c.setName("Class B"); c.setCode("CODEB");
        String createJson = objectMapper.writeValueAsString(c);
        String res = mockMvc.perform(post("/api/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.id", notNullValue()))
            .andReturn().getResponse().getContentAsString();

        Classes created = objectMapper.readValue(res, com.fasterxml.jackson.databind.JsonNode.class)
            .get("data").traverse(objectMapper).readValueAs(Classes.class);

        Classes patch = new Classes(); patch.setDescription("DescB");
        String updateJson = objectMapper.writeValueAsString(patch);
        mockMvc.perform(put("/api/classes/{id}", created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.description", is("DescB")));

        mockMvc.perform(delete("/api/classes/{id}", created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }
}

