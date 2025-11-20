package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.oj.onlinejudge.domain.dto.ClassesRequest;
import com.oj.onlinejudge.domain.entity.Classes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ClassesController 集成测试")
class ClassesControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("未登录访问班级接口返回 401")
    void requireLogin() throws Exception {
        assertUnauthorized(performGet("/api/classes", null, null));
    }

    @Test
    @DisplayName("教师创建班级，学生创建返回 403")
    void teacherCreate_studentForbidden() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        ClassesRequest request = new ClassesRequest();
        request.setName("Class-" + uniqueSuffix());
        request.setCode("CODE" + uniqueSuffix());
        request.setDescription("desc");
        Classes created = readData(performPostJson("/api/classes", request, teacher.token()), Classes.class);
        assertThat(created.getName()).isEqualTo(request.getName());

        AuthSession student = registerAndLoginStudent();
        assertForbidden(performPostJson("/api/classes", request, student.token()));
    }

    @Test
    @DisplayName("教师只能更新自己创建的班级，管理员可更新任意班级")
    void teacherUpdateOwnClass_only() throws Exception {
        AuthSession teacher1 = registerAndLoginTeacher();
        Classes created = createClassAs(teacher1, "CLS-" + uniqueSuffix());
        AuthSession teacher2 = registerAndLoginTeacher();
        ClassesRequest update = new ClassesRequest();
        update.setName("Forbidden");
        assertForbidden(performPutJson("/api/classes/" + created.getId(), update, teacher2.token()));

        AuthSession admin = registerAndLoginAdmin();
        update.setName("AdminUpdated");
        Classes updated =
                readData(performPutJson("/api/classes/" + created.getId(), update, admin.token()), Classes.class);
        assertThat(updated.getName()).isEqualTo("AdminUpdated");
    }

    private Classes createClassAs(AuthSession teacher, String name) throws Exception {
        ClassesRequest request = new ClassesRequest();
        request.setName(name);
        request.setCode("CODE" + uniqueSuffix());
        request.setDescription("desc");
        return readData(performPostJson("/api/classes", request, teacher.token()), Classes.class);
    }
}
