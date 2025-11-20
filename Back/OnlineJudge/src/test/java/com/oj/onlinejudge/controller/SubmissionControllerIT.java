package com.oj.onlinejudge.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.oj.onlinejudge.domain.dto.ProblemTestcaseRequest;
import com.oj.onlinejudge.domain.dto.SubmissionCreateRequest;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.vo.SubmissionDetailVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

@DisplayName("SubmissionController 集成测试")
class SubmissionControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("学生提交代码成功，其他学生无法查看详情，老师可按学号筛选")
    void submitAndList() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        long problemId = createProblemWithTestcase(teacher);

        AuthSession student = registerAndLoginStudent();
        SubmissionCreateRequest request = new SubmissionCreateRequest();
        request.setProblemId(problemId);
        request.setLanguageId(54);
        request.setSourceCode("int main(){return 0;}");
        SubmissionDetailVO detail =
                readData(performPostJson("/api/submissions", request, student.token()), SubmissionDetailVO.class);
        assertThat(detail.getOverallStatusId()).isEqualTo(3);

        // 学生列表仅包含自己
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "1");
        params.add("size", "10");
        JsonNode list = assertOkAndStandardApiResponse(performGet("/api/submissions", params, student.token()))
                .get("data")
                .get("records");
        assertThat(list.findValues("problemId").stream().map(JsonNode::asText).toArray())
                .contains(String.valueOf(problemId));

        // 其他学生查看详情 403
        AuthSession other = registerAndLoginStudent();
        assertForbidden(performGet("/api/submissions/" + detail.getId(), null, other.token()));

        // 教师可按 studentId 查询
        params.add("studentId", String.valueOf(student.id()));
        assertOkAndStandardApiResponse(performGet("/api/submissions", params, teacher.token()));
    }

    @Test
    @DisplayName("提交失败的代码返回非 Accepted 状态")
    void submitFailingCode() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        long problemId = createProblemWithTestcase(teacher);

        AuthSession student = registerAndLoginStudent();
        SubmissionCreateRequest request = new SubmissionCreateRequest();
        request.setProblemId(problemId);
        request.setLanguageId(54);
        request.setSourceCode("fail");
        SubmissionDetailVO detail =
                readData(performPostJson("/api/submissions", request, student.token()), SubmissionDetailVO.class);
        assertThat(detail.getOverallStatusId()).isNotEqualTo(3);
    }

    private long createProblemWithTestcase(AuthSession teacher) throws Exception {
        Problem problem = readData(
                performPostJson("/api/admin/problems", buildProblemRequest(), teacher.token()), Problem.class);
        ProblemTestcaseRequest req = new ProblemTestcaseRequest();
        req.setInputData("1 2");
        req.setOutputData("3");
        performPostJson("/api/admin/problems/" + problem.getId() + "/testcases", req, teacher.token())
                .andExpect(status().isOk());
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
