package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.oj.onlinejudge.domain.dto.ClassesRequest;
import com.oj.onlinejudge.domain.dto.HomeworkProblemBatchRequest;
import com.oj.onlinejudge.domain.dto.HomeworkRequest;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.domain.entity.Homework;
import com.oj.onlinejudge.domain.entity.Problem;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

@DisplayName("HomeworkController 集成测试")
class HomeworkControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("教师创建/更新作业并管理题目，学生创建返回 403")
    void manageHomework() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        Classes clazz = createClass(teacher);
        long problem1 = createProblem(teacher);
        long problem2 = createProblem(teacher);

        HomeworkRequest create = new HomeworkRequest();
        create.setTitle("Week1 Homework");
        create.setClassId(clazz.getId());
        create.setDescription("Initial description");
        create.setProblemIds(List.of(problem1));
        Homework homework = readData(performPostJson("/api/homeworks", create, teacher.token()), Homework.class);

        AuthSession student = registerAndLoginStudent();
        assertForbidden(performPostJson("/api/homeworks", create, student.token()));

        HomeworkRequest update = new HomeworkRequest();
        update.setDescription("Updated description");
        update.setProblemIds(List.of(problem2));
        Homework updated =
                readData(performPutJson("/api/homeworks/" + homework.getId(), update, teacher.token()), Homework.class);
        assertThat(updated.getDescription()).isEqualTo("Updated description");

        HomeworkProblemBatchRequest batch = new HomeworkProblemBatchRequest();
        batch.setProblemIds(List.of(problem1));
        assertOkAndStandardApiResponse(
                performPostJson("/api/homeworks/" + homework.getId() + "/problems", batch, teacher.token()));
        assertOkAndStandardApiResponse(performDelete(
                "/api/homeworks/" + homework.getId() + "/problems/" + problem1, null, teacher.token()));

        assertOkAndStandardApiResponse(performDelete("/api/homeworks/" + homework.getId(), null, teacher.token()));
    }

    @Test
    @DisplayName("学生只能查看作业列表/详情和题目")
    void studentViewHomework() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        Classes clazz = createClass(teacher);
        long problem = createProblem(teacher);
        HomeworkRequest create = new HomeworkRequest();
        create.setTitle("Week2 Homework");
        create.setClassId(clazz.getId());
        create.setProblemIds(List.of(problem));
        Homework homework = readData(performPostJson("/api/homeworks", create, teacher.token()), Homework.class);

        AuthSession student = registerAndLoginStudent();
        assertOkAndStandardApiResponse(performGet("/api/homeworks", null, student.token()));
        assertOkAndStandardApiResponse(performGet("/api/homeworks/" + homework.getId(), null, student.token()));
        assertOkAndStandardApiResponse(
                performGet("/api/homeworks/" + homework.getId() + "/problems", null, student.token()));
    }

    private Classes createClass(AuthSession teacher) throws Exception {
        ClassesRequest request = new ClassesRequest();
        request.setName("Class-" + uniqueSuffix());
        request.setCode("CODE" + uniqueSuffix());
        return readData(performPostJson("/api/classes", request, teacher.token()), Classes.class);
    }

    private long createProblem(AuthSession teacher) throws Exception {
        Problem problem = readData(
                performPostJson("/api/admin/problems", buildProblemRequest(), teacher.token()), Problem.class);
        return problem.getId();
    }

    private com.oj.onlinejudge.domain.dto.ProblemUpsertRequest buildProblemRequest() {
        com.oj.onlinejudge.domain.dto.ProblemUpsertRequest req = new com.oj.onlinejudge.domain.dto.ProblemUpsertRequest();
        req.setName("Problem-" + uniqueSuffix());
        req.setDescription("desc");
        req.setDescriptionInput("input");
        req.setDescriptionOutput("output");
        req.setSampleInput("1 2");
        req.setSampleOutput("3");
        req.setHint("hint");
        req.setDailyChallenge("0");
        req.setDifficulty("easy");
        req.setTimeLimitMs(1000);
        req.setMemoryLimitKb(65536);
        req.setSource("integration-test");
        req.setIsActive(true);
        return req;
    }
}
