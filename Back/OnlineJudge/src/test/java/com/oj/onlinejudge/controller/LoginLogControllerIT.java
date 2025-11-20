package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.oj.onlinejudge.domain.dto.LoginLogRequest;
import com.oj.onlinejudge.domain.entity.LoginLog;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

@DisplayName("LoginLogController 集成测试")
class LoginLogControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("未登录访问登录日志返回 401")
    void requireLogin() throws Exception {
        assertUnauthorized(performGet("/api/login-logs", null, null));
    }

    @Test
    @DisplayName("管理员可以分页筛选登录日志")
    void listLogs_withFilter() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("role", "student");
        assertOkAndStandardApiResponse(performGet("/api/login-logs", params, admin.token()));
    }

    @Test
    @DisplayName("创建、更新、删除登录日志")
    void crudLogs() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        LoginLogRequest create = new LoginLogRequest();
        create.setRole("student");
        create.setUserId(registerAndLoginStudent().id());
        create.setUsername("log-" + uniqueSuffix());
        create.setIpAddress("127.0.0.1");
        create.setLoginTime(LocalDateTime.now());
        LoginLog created = readData(performPostJson("/api/login-logs", create, admin.token()), LoginLog.class);

        LoginLogRequest update = new LoginLogRequest();
        update.setLogoutTime(LocalDateTime.now());
        LoginLog updated = readData(
                performPutJson("/api/login-logs/" + created.getId(), update, admin.token()), LoginLog.class);
        assertThat(updated.getLogoutTime()).isNotNull();

        assertOkAndStandardApiResponse(performDelete("/api/login-logs/" + created.getId(), null, admin.token()));
        assertThat(performGet("/api/login-logs/" + created.getId(), null, admin.token())
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(404);
    }

    @Test
    @DisplayName("创建登录日志缺少必填字段返回 400")
    void createLog_validationError() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        LoginLogRequest create = new LoginLogRequest();
        assertBadRequestAndValidationErrors(performPostJson("/api/login-logs", create, admin.token()));
    }
}
