package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.oj.onlinejudge.domain.dto.AdminUpsertRequest;
import com.oj.onlinejudge.domain.entity.Admin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("AdminController 集成测试")
class AdminControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("未登录访问管理员接口返回 401")
    void list_requiresLogin() throws Exception {
        assertUnauthorized(performGet("/api/admins", null, null));
    }

    @Test
    @DisplayName("管理员 CRUD 流程")
    void adminCrud_success() throws Exception {
        AuthSession session = registerAndLoginAdmin();
        AdminUpsertRequest create = new AdminUpsertRequest();
        create.setUsername(uniqueUsername("admin"));
        create.setPassword(strongPassword("Adm"));
        create.setEmail(uniqueEmail("admin"));
        create.setName("Admin-" + create.getUsername());

        Admin created = readData(performPostJson("/api/admins", create, session.token()), Admin.class);
        assertThat(created.getUsername()).isEqualTo(create.getUsername());

        AdminUpsertRequest update = new AdminUpsertRequest();
        update.setPassword(strongPassword("Upd"));
        update.setName("Updated");
        Admin updated = readData(
                performPutJson("/api/admins/" + created.getId(), update, session.token()), Admin.class);
        assertThat(updated.getName()).isEqualTo("Updated");

        assertOkAndStandardApiResponse(performDelete("/api/admins/" + created.getId(), null, session.token()));
        assertThat(performGet("/api/admins/" + created.getId(), null, session.token())
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(404);
    }

    @Test
    @DisplayName("创建管理员缺少密码返回 400")
    void createAdmin_missingPassword() throws Exception {
        AuthSession session = registerAndLoginAdmin();
        AdminUpsertRequest request = new AdminUpsertRequest();
        request.setUsername(uniqueUsername("admin"));
        assertBadRequestAndValidationErrors(performPostJson("/api/admins", request, session.token()));
    }

    @Test
    @DisplayName("查询不存在管理员返回 404")
    void getAdmin_notFound() throws Exception {
        AuthSession session = registerAndLoginAdmin();
        assertThat(performGet("/api/admins/999999", null, session.token())
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(404);
    }
}
