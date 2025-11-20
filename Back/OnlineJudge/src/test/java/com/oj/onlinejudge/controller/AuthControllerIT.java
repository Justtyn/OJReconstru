package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oj.onlinejudge.domain.dto.ForgotPasswordSendCodeRequest;
import com.oj.onlinejudge.domain.dto.ForgotPasswordVerifyRequest;
import com.oj.onlinejudge.domain.dto.LoginRequest;
import com.oj.onlinejudge.domain.dto.RegisterRequest;
import com.oj.onlinejudge.domain.entity.LoginLog;
import com.oj.onlinejudge.service.LoginLogService;
import com.oj.onlinejudge.service.VerificationCodeService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("AuthController 集成测试")
class AuthControllerIT extends ControllerTestSupport {

    @Autowired private LoginLogService loginLogService;
    @Autowired private VerificationCodeService verificationCodeService;

    @Test
    @DisplayName("注册学生成功并立即返回登录态")
    void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(uniqueUsername("reg"));
        request.setPassword(strongPassword("Reg"));
        request.setEmail(uniqueEmail("reg"));
        request.setName("Reg Tester");

        JsonNode root = assertOkAndStandardApiResponse(performPostJson("/api/auth/register", request, null));
        JsonNode data = root.get("data");
        assertThat(data.get("username").asText()).isEqualTo(request.getUsername());
        assertThat(data.get("role").asText()).isEqualTo("student");
        assertThat(data.get("token").asText()).startsWith("Bearer ");
        assertThat(data.get("details").has("password")).isFalse();

        // 再次注册相同用户名 -> 400
        assertBadRequestAndValidationErrors(performPostJson("/api/auth/register", request, null));
    }

    @Test
    @DisplayName("学生/教师/管理员登录成功")
    void login_multiRoles() throws Exception {
        AuthSession student = registerAndLoginStudent();
        // 再通过 /login 校验
        LoginRequest studentLogin = new LoginRequest();
        studentLogin.setUsername(student.username());
        studentLogin.setPassword(student.rawPassword());
        JsonNode studentRes =
                assertOkAndStandardApiResponse(performPostJson("/api/auth/login", studentLogin, null)).get("data");
        assertThat(studentRes.get("role").asText()).isEqualTo("student");

        AuthSession teacher = registerAndLoginTeacher();
        LoginRequest teacherLogin = new LoginRequest();
        teacherLogin.setUsername(teacher.username());
        teacherLogin.setPassword(teacher.rawPassword());
        teacherLogin.setRole("teacher");
        JsonNode teacherRes =
                assertOkAndStandardApiResponse(performPostJson("/api/auth/login", teacherLogin, null)).get("data");
        assertThat(teacherRes.get("role").asText()).isEqualTo("teacher");

        AuthSession admin = registerAndLoginAdmin();
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setUsername(admin.username());
        adminLogin.setPassword(admin.rawPassword());
        adminLogin.setRole("admin");
        JsonNode adminRes =
                assertOkAndStandardApiResponse(performPostJson("/api/auth/login", adminLogin, null)).get("data");
        assertThat(adminRes.get("role").asText()).isEqualTo("admin");
    }

    @Test
    @DisplayName("用户不存在或密码错误返回 400")
    void login_failures() throws Exception {
        LoginRequest missing = new LoginRequest();
        missing.setUsername("missing-" + uniqueSuffix());
        missing.setPassword("whatever");
        assertBadRequestAndValidationErrors(performPostJson("/api/auth/login", missing, null));

        AuthSession student = registerAndLoginStudent();
        LoginRequest wrongPwd = new LoginRequest();
        wrongPwd.setUsername(student.username());
        wrongPwd.setPassword("Wrong" + student.rawPassword());
        assertBadRequestAndValidationErrors(performPostJson("/api/auth/login", wrongPwd, null));
    }

    @Test
    @DisplayName("携带 Token 获取当前用户信息，未登录返回 401")
    void currentUser_me() throws Exception {
        AuthSession session = registerAndLoginStudent();
        JsonNode me = assertOkAndStandardApiResponse(performGet("/api/auth/users/me", null, session.token()))
                .get("data");
        assertThat(me.get("username").asText()).isEqualTo(session.username());
        assertThat(me.get("role").asText()).isEqualTo("student");
        assertThat(me.get("details").has("password")).isFalse();

        assertUnauthorized(performGet("/api/auth/users/me", null, null));
    }

    @Test
    @DisplayName("注销会补写登录日志登出时间")
    void logout_updatesLoginLog() throws Exception {
        AuthSession session = registerAndLoginStudent();
        assertOkAndStandardApiResponse(performPostJson("/api/auth/logout", null, session.token()));

        LoginLog latest = loginLogService.lambdaQuery()
                .eq(LoginLog::getUserId, session.id())
                .orderByDesc(LoginLog::getLoginTime)
                .last("limit 1")
                .one();
        assertThat(latest).isNotNull();
        assertThat(latest.getLogoutTime()).isNotNull();
    }

    @Nested
    class PasswordChangeTests {

        @Test
        @DisplayName("学生修改密码后需使用新密码登录")
        void changePassword_success() throws Exception {
            AuthSession session = registerAndLoginStudent();
            String newPassword = strongPassword("NewPwd");

            ObjectNode body = objectMapper.createObjectNode();
            body.put("oldPassword", session.rawPassword());
            body.put("newPassword", newPassword);

            assertOkAndStandardApiResponse(
                    performPostJson("/api/auth/password/change", body, session.token()));

            // 旧密码应该失效
            LoginRequest oldLogin = new LoginRequest();
            oldLogin.setUsername(session.username());
            oldLogin.setPassword(session.rawPassword());
            assertBadRequestAndValidationErrors(performPostJson("/api/auth/login", oldLogin, null));

            // 新密码成功
            LoginRequest newLogin = new LoginRequest();
            newLogin.setUsername(session.username());
            newLogin.setPassword(newPassword);
            assertOkAndStandardApiResponse(performPostJson("/api/auth/login", newLogin, null));
        }

        @Test
        @DisplayName("非学生调用修改密码接口返回 401")
        void changePassword_nonStudentRejected() throws Exception {
            AuthSession teacher = registerAndLoginTeacher();
            ObjectNode body = objectMapper.createObjectNode();
            body.put("oldPassword", teacher.rawPassword());
            body.put("newPassword", strongPassword("New"));

            assertUnauthorized(performPostJson("/api/auth/password/change", body, teacher.token()));
        }
    }

    @Nested
    class ForgotPasswordTests {

        @Test
        @DisplayName("发送验证码后可读取验证码缓存条目")
        void sendCode_success() throws Exception {
            AuthSession session = registerAndLoginStudent();
            ForgotPasswordSendCodeRequest req = new ForgotPasswordSendCodeRequest();
            req.setUsername(session.username());
            assertOkAndStandardApiResponse(
                    performPostJson("/api/auth/password/forgot/sendCode", req, null));

            Optional<VerificationCodeService.Pending> pending =
                    verificationCodeService.get("pwd:" + session.username());
            assertThat(pending).isPresent();
            assertThat(pending.get().code).hasSize(6);
        }

        @Test
        @DisplayName("验证码验证后重置密码并可使用新密码登录")
        void verifyAndReset_success() throws Exception {
            AuthSession session = registerAndLoginStudent();
            sendForgotCode(session);
            VerificationCodeService.Pending pending =
                    verificationCodeService.get("pwd:" + session.username()).orElseThrow();

            ForgotPasswordVerifyRequest verifyRequest = new ForgotPasswordVerifyRequest();
            verifyRequest.setUsername(session.username());
            verifyRequest.setCode(pending.code);
            String newPassword = strongPassword("Reset");
            verifyRequest.setNewPassword(newPassword);

            assertOkAndStandardApiResponse(
                    performPostJson("/api/auth/password/forgot/verify", verifyRequest, null));

            LoginRequest login = new LoginRequest();
            login.setUsername(session.username());
            login.setPassword(newPassword);
            assertOkAndStandardApiResponse(performPostJson("/api/auth/login", login, null));
        }

        @Test
        @DisplayName("验证码错误或账号不存在时返回 400")
        void verify_failure() throws Exception {
            // account missing
            ForgotPasswordSendCodeRequest send = new ForgotPasswordSendCodeRequest();
            send.setUsername("missing-" + uniqueSuffix());
            assertBadRequestAndValidationErrors(
                    performPostJson("/api/auth/password/forgot/sendCode", send, null));

            AuthSession session = registerAndLoginStudent();
            sendForgotCode(session);
            ForgotPasswordVerifyRequest verifyRequest = new ForgotPasswordVerifyRequest();
            verifyRequest.setUsername(session.username());
            verifyRequest.setCode("WRONG");
            verifyRequest.setNewPassword(strongPassword("Reset"));
            assertBadRequestAndValidationErrors(
                    performPostJson("/api/auth/password/forgot/verify", verifyRequest, null));
        }

        private void sendForgotCode(AuthSession session) throws Exception {
            ForgotPasswordSendCodeRequest req = new ForgotPasswordSendCodeRequest();
            req.setUsername(session.username());
            assertOkAndStandardApiResponse(
                    performPostJson("/api/auth/password/forgot/sendCode", req, null));
        }
    }
}
