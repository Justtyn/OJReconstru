package com.oj.onlinejudge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.oj.onlinejudge.domain.entity.Classes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClassesControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("班级-分页列表")
    void listClasses() throws Exception {
        String tk = registerStudent().token();
        createClass(tk);
        authed(get("/api/classes").param("page", "1").param("size", "5"), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.records", notNullValue()));
    }

    @Test
    @DisplayName("班级-详情不存在")
    void getClass_notFound() throws Exception {
        String tk = registerStudent().token();
        authed(get("/api/classes/{id}", 9999), tk)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    @DisplayName("班级-创建/更新/删除")
    void crudClass() throws Exception {
        String tk = registerStudent().token();
        JsonNode created = createClass(tk);

        Classes patch = new Classes();
        patch.setDescription("DescB");
        authed(put("/api/classes/{id}", created.get("id").asLong())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patch)), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.description", is("DescB")));

        authed(delete("/api/classes/{id}", created.get("id").asLong()), tk)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }

    private JsonNode createClass(String token) throws Exception {
        Classes c = new Classes();
        c.setName("Class-" + uniqueLabel("name"));
        c.setCode("CODE-" + uniqueLabel("code"));
        return readJson(authed(post("/api/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)), token)).get("data");
    }
}
