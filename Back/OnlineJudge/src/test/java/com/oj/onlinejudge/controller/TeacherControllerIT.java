package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.oj.onlinejudge.domain.dto.TeacherUpsertRequest;
import com.oj.onlinejudge.domain.entity.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TeacherController 集成测试")
class TeacherControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("未登录访问教师接口返回 401")
    void requireLogin() throws Exception {
        assertUnauthorized(performGet("/api/teachers", null, null));
    }

    @Test
    @DisplayName("创建并更新教师成功")
    void createAndUpdateTeacher() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        TeacherUpsertRequest create = new TeacherUpsertRequest();
        create.setUsername(uniqueUsername("teacher"));
        create.setPassword(strongPassword("Teach"));
        create.setEmail(uniqueEmail("teacher"));
        create.setName("Teacher-" + create.getUsername());
        Teacher created = readData(performPostJson("/api/teachers", create, admin.token()), Teacher.class);

        TeacherUpsertRequest update = new TeacherUpsertRequest();
        update.setName("Updated");
        Teacher updated = readData(
                performPutJson("/api/teachers/" + created.getId(), update, admin.token()), Teacher.class);
        assertThat(updated.getName()).isEqualTo("Updated");

        assertOkAndStandardApiResponse(performDelete("/api/teachers/" + created.getId(), null, admin.token()));
    }

    @Test
    @DisplayName("创建教师缺少密码返回 400")
    void createTeacher_missingPassword() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        TeacherUpsertRequest request = new TeacherUpsertRequest();
        request.setUsername(uniqueUsername("teacher"));
        assertBadRequestAndValidationErrors(performPostJson("/api/teachers", request, admin.token()));
    }

    @Test
    @DisplayName("更新不存在教师返回 404")
    void updateTeacher_notFound() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        TeacherUpsertRequest update = new TeacherUpsertRequest();
        update.setName("Ghost");
        assertThat(performPutJson("/api/teachers/999999", update, admin.token())
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(404);
    }
}
