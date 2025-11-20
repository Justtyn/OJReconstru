package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oj.onlinejudge.domain.entity.Announcement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AnnouncementController & AnnouncementAdminController 集成测试")
class AnnouncementControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("公告公开列表需要登录，所有角色可查看")
    void listAnnouncements_requiresLogin() throws Exception {
        assertUnauthorized(performGet("/api/announcements", null, null));

        AuthSession student = registerAndLoginStudent();
        assertOkAndStandardApiResponse(performGet("/api/announcements", null, student.token()));
    }

    @Test
    @DisplayName("管理员 CRUD 公告，其他角色写返回 403")
    void adminCrud() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        ObjectNode create = objectMapper.createObjectNode();
        create.put("title", "Midterm Notice");
        create.put("content", "Please check the exam schedule.");
        create.put("isPinned", true);
        Announcement created = readData(performPostJson("/api/admin/announcements", create, admin.token()),
                Announcement.class);

        AuthSession student = registerAndLoginStudent();
        assertForbidden(performPostJson("/api/admin/announcements", create, student.token()));

        ObjectNode update = objectMapper.createObjectNode();
        update.put("title", "Midterm Notice Updated");
        update.put("content", "Exam is postponed by one week.");
        Announcement updated = readData(
                performPutJson("/api/admin/announcements/" + created.getId(), update, admin.token()),
                Announcement.class);

        JsonNode detail = assertOkAndStandardApiResponse(
                performGet("/api/announcements/" + updated.getId(), null, student.token()))
                .get("data");
        assertThat(detail.get("title").asText()).isEqualTo("Midterm Notice Updated");

        assertOkAndStandardApiResponse(
                performDelete("/api/admin/announcements/" + updated.getId(), null, admin.token()));
        assertThat(performGet("/api/announcements/" + updated.getId(), null, admin.token())
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(404);
    }
}
