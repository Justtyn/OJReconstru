package com.oj.onlinejudge.controller;

import com.oj.onlinejudge.domain.dto.RegisterRequest;
import com.oj.onlinejudge.domain.dto.ResendRegisterCodeRequest;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.security.PasswordService;
import com.oj.onlinejudge.service.StudentService;
import com.oj.onlinejudge.service.VerificationCodeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "app.auth.require-email-verify=true")
class AuthControllerRegisterVerifyTest extends ControllerTestSupport {

    @Autowired
    VerificationCodeService verificationCodeService;
    @Autowired
    StudentService studentService;
    @Autowired
    PasswordService passwordService;

    @Test
    @DisplayName("注册重发验证码-用户名密码匹配时刷新验证码")
    void resendRegisterCode_success() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setUsername(uniqueUsername("resend"));
        register.setPassword("Resend123");
        register.setEmail(uniqueEmail("resend"));
        register.setName("Resend User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(register)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        VerificationCodeService.Pending pending = verificationCodeService.get(register.getUsername())
                .orElseThrow(() -> new IllegalStateException("pending missing after register"));
        String oldCode = pending.code;

        ResendRegisterCodeRequest resend = new ResendRegisterCodeRequest();
        resend.setUsername(register.getUsername());
        resend.setPassword(register.getPassword());

        mockMvc.perform(post("/api/auth/register/resendCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(resend)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        VerificationCodeService.Pending updated = verificationCodeService.get(register.getUsername())
                .orElseThrow(() -> new IllegalStateException("pending missing after resend"));

        Assertions.assertThat(updated.code).isNotBlank();
        Assertions.assertThat(updated.expireEpochMillis).isGreaterThan(System.currentTimeMillis());
        if (oldCode != null) {
            Assertions.assertThat(updated.code).isNotEqualTo(oldCode);
        }
    }

    @Test
    @DisplayName("已存在未验证账号可重发验证码并完成激活")
    void resendForExistingUnverified() throws Exception {
        String username = uniqueUsername("existing");
        String rawPassword = "ExistPwd123";
        Student student = new Student();
        student.setUsername(username);
        student.setPassword(passwordService.encode(rawPassword));
        student.setEmail(uniqueEmail("existing"));
        student.setName("Existing User");
        student.setIsVerified(false);
        studentService.save(student);

        ResendRegisterCodeRequest resend = new ResendRegisterCodeRequest();
        resend.setUsername(username);
        resend.setPassword(rawPassword);
        mockMvc.perform(post("/api/auth/register/resendCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(resend)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        VerificationCodeService.Pending pending = verificationCodeService.get(username)
                .orElseThrow(() -> new IllegalStateException("pending missing for existing user"));

        com.fasterxml.jackson.databind.node.ObjectNode verifyReq = objectMapper.createObjectNode();
        verifyReq.put("username", username);
        verifyReq.put("code", pending.code);

        mockMvc.perform(post("/api/auth/verifyEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.username", is(username)));

        Student refreshed = studentService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("student not found after verify"));
        Assertions.assertThat(refreshed.getIsVerified()).isTrue();
    }
}
