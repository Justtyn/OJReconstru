package com.oj.onlinejudge.controller;

import static org.hamcrest.Matchers.equalTo;
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

class DiscussionControllerTest extends ControllerTestSupport {

    @Autowired
    private ProblemService problemService;

    @Test
    @DisplayName("学生发讨论、其他学生评论，作者与管理员权限受控")
    void discussionCrudFlow() throws Exception {
        TestUser student = registerStudent();
        TestUser student2 = registerStudent();
        TestUser admin = createAdminUser();

        Long problemId = createProblemFixture();

        ObjectNode createReq = objectMapper.createObjectNode();
        createReq.put("title", "算法讨论");
        createReq.put("content", "请问这题怎么做？");
        createReq.put("problemId", problemId);

        JsonNode created = readJson(authed(post("/api/discussions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0))));
        long discussionId = created.get("data").get("id").asLong();

        // 其他学生不能修改
        ObjectNode updateReq = objectMapper.createObjectNode();
        updateReq.put("title", "非法更新");
        authed(put("/api/discussions/{id}", discussionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)), student2.token())
                .andExpect(status().isForbidden());

        // 作者更新
        updateReq.put("title", "算法讨论-更新");
        updateReq.put("content", "我找到一种做法。");
        authed(put("/api/discussions/{id}", discussionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("算法讨论-更新")));

        // 学生评论
        ObjectNode commentReq = objectMapper.createObjectNode();
        commentReq.put("content", "可以试试双指针。");
        JsonNode comment = readJson(authed(post("/api/discussions/{id}/comments", discussionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentReq)), student2.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0))));
        long commentId = comment.get("data").get("id").asLong();

        // 学生删除自己的评论
        authed(delete("/api/discussions/comments/{id}", commentId), student2.token())
                .andExpect(status().isOk());

        // 管理员删除讨论
        authed(delete("/api/discussions/{id}", discussionId), admin.token())
                .andExpect(status().isOk());

        authed(get("/api/discussions/{id}", discussionId), student.token())
                .andExpect(status().isNotFound());
    }

    private Long createProblemFixture() {
        Problem problem = new Problem();
        problem.setName("Discuss-Problem-" + uniqueLabel("prob"));
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
