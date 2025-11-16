package com.oj.onlinejudge.controller;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AnnouncementControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("管理员可增删改公告，教师/学生可查看")
    void adminCrudAnnouncementsAndPublicView() throws Exception {
        TestUser admin = createAdminUser();
        TestUser teacher = createTeacherUser();
        TestUser student = registerStudent();

        ObjectNode createReq = objectMapper.createObjectNode();
        createReq.put("title", "期中考试安排");
        createReq.put("content", "请查看考试时间。");
        createReq.put("isPinned", true);

        JsonNode created = readJson(authed(post("/api/admin/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)), admin.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0))));
        long announcementId = created.get("data").get("id").asLong();

        // 非管理员无法创建
        authed(post("/api/admin/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)), teacher.token())
                .andExpect(status().isForbidden());

        ObjectNode updateReq = objectMapper.createObjectNode();
        updateReq.put("title", "期中考试安排-更新");
        updateReq.put("content", "考试地点更新，请关注。");
        updateReq.put("isPinned", false);
        authed(put("/api/admin/announcements/{id}", announcementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)), admin.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("期中考试安排-更新")));

        authed(get("/api/announcements"), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.total", greaterThan(0)));

        authed(get("/api/announcements/{id}", announcementId), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("期中考试安排-更新")));

        authed(delete("/api/admin/announcements/{id}", announcementId), admin.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        authed(get("/api/announcements/{id}", announcementId), student.token())
                .andExpect(status().isNotFound());
    }
}
