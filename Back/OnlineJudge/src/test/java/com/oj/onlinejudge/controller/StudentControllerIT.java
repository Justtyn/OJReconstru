package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.oj.onlinejudge.domain.dto.StudentUpsertRequest;
import com.oj.onlinejudge.domain.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StudentController 集成测试")
class StudentControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("未登录访问学生接口返回 401")
    void requireLogin() throws Exception {
        assertUnauthorized(performGet("/api/students", null, null));
    }

    @Test
    @DisplayName("创建学生后可使用密码登录")
    void createStudent_andLogin() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        StudentUpsertRequest request = new StudentUpsertRequest();
        request.setUsername(uniqueUsername("stu"));
        request.setPassword(strongPassword("Stu"));
        request.setEmail(uniqueEmail("stu"));
        request.setName("Student-" + request.getUsername());
        Student created = readData(performPostJson("/api/students", request, admin.token()), Student.class);
        assertThat(created.getUsername()).isEqualTo(request.getUsername());

        AuthSession logged = loginSession(request.getUsername(), request.getPassword());
        assertThat(logged.user().getRole()).isEqualTo("student");
    }

    @Test
    @DisplayName("更新学生为空密码会保留旧密码")
    void updateStudent_keepPassword() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        StudentUpsertRequest request = new StudentUpsertRequest();
        request.setUsername(uniqueUsername("stu"));
        request.setPassword(strongPassword("Stu"));
        request.setName("Student");
        Student created = readData(performPostJson("/api/students", request, admin.token()), Student.class);

        StudentUpsertRequest update = new StudentUpsertRequest();
        update.setName("Renamed");
        readData(performPutJson("/api/students/" + created.getId(), update, admin.token()), Student.class);

        // should still login using old password
        AuthSession logged = loginSession(request.getUsername(), request.getPassword());
        assertThat(logged.user().getDetails().get("name")).isEqualTo("Renamed");
    }

    @Test
    @DisplayName("删除学生后获取返回 404")
    void deleteStudent() throws Exception {
        AuthSession admin = registerAndLoginAdmin();
        StudentUpsertRequest request = new StudentUpsertRequest();
        request.setUsername(uniqueUsername("stu"));
        request.setPassword(strongPassword("Stu"));
        Student created = readData(performPostJson("/api/students", request, admin.token()), Student.class);

        assertOkAndStandardApiResponse(performDelete("/api/students/" + created.getId(), null, admin.token()));
        assertThat(performGet("/api/students/" + created.getId(), null, admin.token())
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(404);
    }
}
