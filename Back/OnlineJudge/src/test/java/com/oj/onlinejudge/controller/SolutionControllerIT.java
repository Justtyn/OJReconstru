package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.oj.onlinejudge.domain.dto.SolutionRequest;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.Solution;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

@DisplayName("SolutionController 集成测试")
class SolutionControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("未登录访问题解列表返回 401")
    void listRequiresLogin() throws Exception {
        assertUnauthorized(performGet("/api/solutions", null, null));
    }

    @Test
    @DisplayName("学生创建题解，作者可编辑，非作者 403，管理员可管理所有题解")
    void solutionCrudWithPermissions() throws Exception {
        AuthSession teacher = registerAndLoginTeacher();
        long problemId = createProblem(teacher);

        AuthSession student1 = registerAndLoginStudent();
        SolutionRequest create = new SolutionRequest();
        create.setTitle("题解一");
        create.setContent("思路 A");
        create.setLanguage("cpp");
        Solution created = readData(
                performPostJson("/api/problems/" + problemId + "/solutions", create, student1.token()),
                Solution.class);
        assertThat(created.getUserId()).isEqualTo(student1.id());

        AuthSession student2 = registerAndLoginStudent();
        SolutionRequest updateByOther = new SolutionRequest();
        updateByOther.setTitle("Malicious edit");
        updateByOther.setContent("Trying to overwrite.");
        assertForbidden(performPutJson("/api/solutions/" + created.getId(), updateByOther, student2.token()));

        AuthSession admin = registerAndLoginAdmin();
        SolutionRequest adminUpdate = new SolutionRequest();
        adminUpdate.setTitle("Admin updated");
        adminUpdate.setContent("Approved content");
        adminUpdate.setIsActive(false);
        Solution updated =
                readData(performPutJson("/api/solutions/" + created.getId(), adminUpdate, admin.token()), Solution.class);
        assertThat(updated.getTitle()).isEqualTo("Admin updated");

        // 普通学生访问已下线题解返回 404，管理员可见
        assertThat(performGet("/api/solutions/" + created.getId(), null, student2.token())
                .andReturn()
                .getResponse()
                .getStatus())
                .isEqualTo(404);
        assertOkAndStandardApiResponse(performGet("/api/solutions/" + created.getId(), null, admin.token()));

        assertOkAndStandardApiResponse(performDelete("/api/solutions/" + created.getId(), null, admin.token()));
    }

    private long createProblem(AuthSession teacher) throws Exception {
        Problem problem = readData(performPostJson("/api/admin/problems", buildProblemRequest(), teacher.token()),
                Problem.class);
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
