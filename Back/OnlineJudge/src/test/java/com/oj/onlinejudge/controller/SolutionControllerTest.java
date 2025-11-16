package com.oj.onlinejudge.controller;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.service.ProblemService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class SolutionControllerTest extends ControllerTestSupport {

    @Autowired
    private ProblemService problemService;

    @Test
    @DisplayName("学生题解 CRUD + 权限校验")
    void studentCrudSolutionWithPermissionGuard() throws Exception {
        Long problemId = createProblemFixture("two-sum");
        TestUser student = registerStudent();
        TestUser student2 = registerStudent();
        TestUser teacher = createTeacherUser();
        TestUser admin = createAdminUser();

        ObjectNode createReq = objectMapper.createObjectNode();
        createReq.put("title", "Two Sum 解法");
        createReq.put("content", "使用哈希表 O(n)。");
        createReq.put("language", "cpp");

        JsonNode created = readJson(authed(post("/api/problems/{problemId}/solutions", problemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0))));
        long solutionId = created.get("data").get("id").asLong();

        // 教师无法创建题解
        authed(post("/api/problems/{problemId}/solutions", problemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)), teacher.token())
                .andExpect(status().isForbidden());

        // 列表/详情
        authed(get("/api/problems/{problemId}/solutions", problemId), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", greaterThanOrEqualTo(1)));

        authed(get("/api/solutions/{id}", solutionId), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Two Sum 解法")));

        // 非作者学生无法修改
        ObjectNode updateReq = objectMapper.createObjectNode();
        updateReq.put("title", "Two Sum 改进版");
        updateReq.put("content", "增加空间优化。");
        updateReq.put("language", "java");
        authed(put("/api/solutions/{id}", solutionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)), student2.token())
                .andExpect(status().isForbidden());

        authed(put("/api/solutions/{id}", solutionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.language", is("java")));

        // 学生删除自己的题解
        authed(delete("/api/solutions/{id}", solutionId), student.token())
                .andExpect(status().isOk());

        // 再创建一条，管理员删除
        JsonNode created2 = readJson(authed(post("/api/problems/{problemId}/solutions", problemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0))));
        long solutionId2 = created2.get("data").get("id").asLong();

        authed(delete("/api/solutions/{id}", solutionId2), admin.token())
                .andExpect(status().isOk());

        authed(get("/api/solutions/{id}", solutionId2), student.token())
                .andExpect(status().isNotFound());
    }

    private Long createProblemFixture(String name) {
        Problem problem = new Problem();
        problem.setName(name + "-" + uniqueLabel("prob"));
        problem.setDescription("desc");
        problem.setDescriptionInput("input");
        problem.setDescriptionOutput("output");
        problem.setSampleInput("1 2");
        problem.setSampleOutput("3");
        problem.setHint("hint");
        problem.setDailyChallenge("0");
        problem.setDifficulty("easy");
        problem.setTimeLimitMs(1000);
        problem.setMemoryLimitKb(131072);
        problem.setIsActive(true);
        problemService.save(problem);
        Assertions.assertThat(problem.getId()).isNotNull();
        return problem.getId();
    }
}
