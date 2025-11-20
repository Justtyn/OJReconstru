package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.oj.onlinejudge.domain.dto.ClassesRequest;
import com.oj.onlinejudge.domain.dto.StudentJoinClassRequest;
import com.oj.onlinejudge.domain.entity.Classes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StudentClassController 集成测试")
class StudentClassControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("学生通过邀请码加入班级并可查询当前班级")
    void joinAndQueryClass() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        Classes clazz = createClassWithCode(teacher);
        AuthSession student = registerAndLoginStudent();

        StudentJoinClassRequest join = new StudentJoinClassRequest();
        join.setCode(clazz.getCode());
        readData(performPostJson("/api/student/classes/join", join, student.token()), Classes.class);

        Classes current = readData(performGet("/api/student/classes", null, student.token()), Classes.class);
        assertThat(current.getId()).isEqualTo(clazz.getId());
    }

    @Test
    @DisplayName("学生只能加入一个班级，再次加入返回 409")
    void joinClass_conflict() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        Classes clazz1 = createClassWithCode(teacher);
        AuthSession student = registerAndLoginStudent();
        StudentJoinClassRequest join = new StudentJoinClassRequest();
        join.setCode(clazz1.getCode());
        performPostJson("/api/student/classes/join", join, student.token()).andExpect(status().isOk());

        Classes clazz2 = createClassWithCode(registerAndLoginTeacher());
        join.setCode(clazz2.getCode());
        assertThat(performPostJson("/api/student/classes/join", join, student.token())
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(409);
    }

    @Test
    @DisplayName("学生退出班级后再次查询返回 404")
    void leaveClass() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        Classes clazz = createClassWithCode(teacher);
        AuthSession student = registerAndLoginStudent();
        StudentJoinClassRequest join = new StudentJoinClassRequest();
        join.setCode(clazz.getCode());
        performPostJson("/api/student/classes/join", join, student.token()).andExpect(status().isOk());

        assertOkAndStandardApiResponse(performPostJson("/api/student/classes/leave", null, student.token()));
        assertThat(performGet("/api/student/classes", null, student.token())
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(404);
    }

    @Test
    @DisplayName("未登录访问学生班级接口返回 401")
    void requireLogin() throws Exception {
        StudentJoinClassRequest req = new StudentJoinClassRequest();
        req.setCode("DUMMY");
        assertUnauthorized(performPostJson("/api/student/classes/join", req, null));
    }

    private Classes createClassWithCode(AuthSession teacher) throws Exception {
        ClassesRequest request = new ClassesRequest();
        request.setName("Class-" + uniqueSuffix());
        request.setCode("CODE" + uniqueSuffix());
        return readData(performPostJson("/api/classes", request, teacher.token()), Classes.class);
    }
}
