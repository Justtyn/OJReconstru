package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.oj.onlinejudge.domain.dto.ClassesMemberRequest;
import com.oj.onlinejudge.domain.dto.ClassesRequest;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.domain.entity.ClassesMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

@DisplayName("ClassesMemberController 集成测试")
class ClassesMemberControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("教师查询成员必须携带 classId，否则 400")
    void teacherList_requiresClassId() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        assertBadRequestAndValidationErrors(performGet("/api/classes-members", null, teacher.token()));
    }

    @Test
    @DisplayName("教师只能管理自己班级成员，管理员可管理任意班级")
    void manageMembers_withPermissions() throws Exception {
        AuthSession teacher1 = registerAndLoginTeacher();
        Classes class1 = createClass(teacher1);
        AuthSession student = registerAndLoginStudent();

        ClassesMemberRequest request = new ClassesMemberRequest();
        request.setClassId(class1.getId());
        request.setMemberType("student");
        request.setStudentId(student.id());
        ClassesMember added =
                readData(performPostJson("/api/classes-members", request, teacher1.token()), ClassesMember.class);
        assertThat(added.getStudentId()).isEqualTo(student.id());

        AuthSession teacher2 = registerAndLoginTeacher();
        request.setClassId(class1.getId());
        request.setStudentId(student.id());
        assertForbidden(performPostJson("/api/classes-members", request, teacher2.token()));

        AuthSession admin = registerAndLoginAdmin();
        request.setClassId(class1.getId());
        request.setStudentId(registerAndLoginStudent().id());
        readData(performPostJson("/api/classes-members", request, admin.token()), ClassesMember.class);
    }

    @Test
    @DisplayName("删除成员成功后列表中不存在")
    void deleteMember() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        Classes clazz = createClass(teacher);
        AuthSession student = registerAndLoginStudent();
        ClassesMember member = addStudentToClass(teacher, clazz, student);

        assertOkAndStandardApiResponse(performDelete("/api/classes-members/" + member.getId(), null, teacher.token()));
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("classId", String.valueOf(clazz.getId()));
        JsonNode list = assertOkAndStandardApiResponse(performGet("/api/classes-members", params, teacher.token()))
                .get("data")
                .get("records");
        assertThat(list).isNotNull();
        assertThat(list.toString()).doesNotContain("\"id\":" + member.getId());
    }

    private Classes createClass(AuthSession teacher) throws Exception {
        ClassesRequest request = new ClassesRequest();
        request.setName("Class-" + uniqueSuffix());
        request.setCode("CODE" + uniqueSuffix());
        return readData(performPostJson("/api/classes", request, teacher.token()), Classes.class);
    }

    private ClassesMember addStudentToClass(AuthSession teacher, Classes clazz, AuthSession student) throws Exception {
        ClassesMemberRequest request = new ClassesMemberRequest();
        request.setClassId(clazz.getId());
        request.setMemberType("student");
        request.setStudentId(student.id());
        return readData(performPostJson("/api/classes-members", request, teacher.token()), ClassesMember.class);
    }
}
