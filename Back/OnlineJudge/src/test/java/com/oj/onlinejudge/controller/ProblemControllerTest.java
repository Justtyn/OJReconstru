package com.oj.onlinejudge.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oj.onlinejudge.domain.entity.ProblemTestcase;
import com.oj.onlinejudge.service.ProblemTestcaseService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class ProblemControllerTest extends ControllerTestSupport {

    @Autowired
    private ProblemTestcaseService problemTestcaseService;

    @Test
    @DisplayName("题库公开列表与详情只返回启用题目")
    void publicProblemListAndDetail() throws Exception {
        TestUser teacher = createTeacherUser();
        String name = "TwoSum-" + uniqueLabel("prob");
        long problemId = createProblem(teacher.token(), name, true);

        mockMvc.perform(get("/api/problems")
                        .param("page", "1")
                        .param("size", "5")
                        .param("keyword", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.records[0].name", is(name)));

        mockMvc.perform(get("/api/problems/{id}", problemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.name", is(name)))
                .andExpect(jsonPath("$.data.description", notNullValue()));
    }

    @Test
    @DisplayName("教师管理题目测试用例 CRUD 并在删除题目时级联清空")
    void manageProblemTestcases() throws Exception {
        TestUser teacher = createTeacherUser();
        String name = "Graph-" + uniqueLabel("prob");
        long problemId = createProblem(teacher.token(), name, false);

        long testcaseId = createTestcase(teacher.token(), problemId, "1 2", "3");

        authed(get("/api/admin/problems/{id}/testcases", problemId), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].inputData", is("1 2")))
                .andExpect(jsonPath("$.data[0].outputData", is("3")));

        ObjectNode updateReq = objectMapper.createObjectNode();
        updateReq.put("inputData", "4 5");
        updateReq.put("outputData", "9");
        authed(put("/api/admin/problem-testcases/{testcaseId}", testcaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.outputData", is("9")));

        authed(delete("/api/admin/problem-testcases/{testcaseId}", testcaseId), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 重新创建测试用例再做级联删除校验
        long cascadeTestcaseId = createTestcase(teacher.token(), problemId, "6", "6");
        Assertions.assertThat(cascadeTestcaseId).isPositive();

        authed(delete("/api/admin/problems/{id}", problemId), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        long remaining = problemTestcaseService.lambdaQuery()
                .eq(ProblemTestcase::getProblemId, problemId)
                .count();
        Assertions.assertThat(remaining).isZero();
    }

    private long createProblem(String token, String name, boolean active) throws Exception {
        ObjectNode req = objectMapper.createObjectNode();
        req.put("name", name);
        req.put("description", "desc-" + name);
        req.put("descriptionInput", "输入描述-" + name);
        req.put("descriptionOutput", "输出描述-" + name);
        req.put("sampleInput", "1 2");
        req.put("sampleOutput", "3");
        req.put("hint", "hint-" + name);
        req.put("dailyChallenge", "0");
        req.put("difficulty", "easy");
        req.put("timeLimitMs", 1000);
        req.put("memoryLimitKb", 131072);
        req.put("source", "unit-test");
        req.put("isActive", active);

        JsonNode resp = readJson(authed(post("/api/admin/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0))));
        return resp.get("data").get("id").asLong();
    }

    private long createTestcase(String token, long problemId, String input, String output) throws Exception {
        ObjectNode req = objectMapper.createObjectNode();
        req.put("inputData", input);
        req.put("outputData", output);
        JsonNode resp = readJson(authed(post("/api/admin/problems/{id}/testcases", problemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0))));
        return resp.get("data").get("id").asLong();
    }
}
