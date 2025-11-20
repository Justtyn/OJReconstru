package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oj.onlinejudge.domain.dto.ProblemTestcaseRequest;
import com.oj.onlinejudge.domain.dto.ProblemUpsertRequest;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.ProblemTestcase;
import com.oj.onlinejudge.service.ProblemTestcaseService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;

@DisplayName("ProblemController / ProblemAdminController 集成测试")
class ProblemControllerIT extends ControllerTestSupport {

    @Autowired private ProblemTestcaseService problemTestcaseService;

    @Test
    @DisplayName("匿名访问题库列表与详情仅返回启用题目")
    void publicProblemListAndDetail() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        long activeProblem = createProblem(teacher, true);
        long inactiveProblem = createProblem(teacher, false);

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "1");
        params.add("size", "10");
        JsonNode records = assertOkAndStandardApiResponse(performGet("/api/problems", params, null))
                .get("data")
                .get("records");
        assertThat(records.findValues("id").stream().map(JsonNode::asText).toArray())
                .contains(String.valueOf(activeProblem));
        assertThat(records.findValues("id").stream().map(JsonNode::asText).toArray())
                .doesNotContain(String.valueOf(inactiveProblem));

        assertOkAndStandardApiResponse(performGet("/api/problems/" + activeProblem, null, null));
        assertThat(performGet("/api/problems/" + inactiveProblem, null, null)
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(404);
    }

    @Test
    @DisplayName("教师/管理员才能管理题目与测试用例")
    void manageProblems_requiresRole() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        long problemId = createProblem(teacher, true);

        // 学生尝试创建题目 -> 403
        AuthSession student = registerAndLoginStudent();
        ProblemUpsertRequest req = buildProblemRequest();
        assertForbidden(performPostJson("/api/admin/problems", req, student.token()));

        // 测试用例 CRUD
        ProblemTestcaseRequest createTc = new ProblemTestcaseRequest();
        createTc.setInputData("1 2");
        createTc.setOutputData("3");
        ProblemTestcase testcase = readData(
                performPostJson("/api/admin/problems/" + problemId + "/testcases", createTc, teacher.token()),
                ProblemTestcase.class);
        assertThat(testcase.getId()).isNotNull();

        ProblemTestcaseRequest updateTc = new ProblemTestcaseRequest();
        updateTc.setInputData("4 5");
        updateTc.setOutputData("9");
        ProblemTestcase updated = readData(
                performPutJson("/api/admin/problem-testcases/" + testcase.getId(), updateTc, teacher.token()),
                ProblemTestcase.class);
        assertThat(updated.getOutputData()).isEqualTo("9");

        assertOkAndStandardApiResponse(
                performDelete("/api/admin/problem-testcases/" + testcase.getId(), null, teacher.token()));
    }

    @Test
    @DisplayName("删除题目同时删除关联测试用例")
    void deleteProblemCascadeTestcases() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        long problemId = createProblem(teacher, true);
        for (int i = 0; i < 2; i++) {
            ProblemTestcaseRequest req = new ProblemTestcaseRequest();
            req.setInputData("in" + i);
            req.setOutputData("out" + i);
            performPostJson("/api/admin/problems/" + problemId + "/testcases", req, teacher.token())
                    .andExpect(status().isOk());
        }
        assertOkAndStandardApiResponse(performDelete("/api/admin/problems/" + problemId, null, teacher.token()));
        long count = problemTestcaseService.lambdaQuery()
                .eq(ProblemTestcase::getProblemId, problemId)
                .count();
        assertThat(count).isZero();
    }

    private long createProblem(AuthSession teacher, boolean active) throws Exception {
        ProblemUpsertRequest request = buildProblemRequest();
        request.setIsActive(active);
        Problem created = readData(performPostJson("/api/admin/problems", request, teacher.token()), Problem.class);
        assertThat(created.getIsActive()).isEqualTo(active);
        return created.getId();
    }

    private ProblemUpsertRequest buildProblemRequest() {
        ProblemUpsertRequest request = new ProblemUpsertRequest();
        request.setName("Problem-" + uniqueSuffix());
        request.setDescription("desc");
        request.setDescriptionInput("input");
        request.setDescriptionOutput("output");
        request.setSampleInput("1 2");
        request.setSampleOutput("3");
        request.setHint("hint");
        request.setDailyChallenge("0");
        request.setDifficulty("easy");
        request.setTimeLimitMs(1000);
        request.setMemoryLimitKb(65536);
        request.setSource("integration-test");
        request.setIsActive(true);
        return request;
    }
}
