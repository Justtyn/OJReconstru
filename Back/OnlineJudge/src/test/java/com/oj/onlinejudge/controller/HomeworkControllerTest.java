package com.oj.onlinejudge.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.service.ClassesService;
import com.oj.onlinejudge.service.ProblemService;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class HomeworkControllerTest extends ControllerTestSupport {

    @Autowired
    private ClassesService classesService;

    @Autowired
    private ProblemService problemService;

    @Test
    @DisplayName("教师创建/管理作业，学生查看列表与题目")
    void teacherManageHomeworkStudentView() throws Exception {
        TestUser teacher = createTeacherUser();
        TestUser student = registerStudent();

        Long classId = createClassForTeacher(teacher.id());
        Long problemA = createProblemFixture("hw-prob-A");
        Long problemB = createProblemFixture("hw-prob-B");
        Long problemC = createProblemFixture("hw-prob-C");

        ObjectNode createReq = objectMapper.createObjectNode();
        createReq.put("title", "第一次作业");
        createReq.put("classId", classId);
        createReq.put("startTime", LocalDateTime.now().minusDays(1).toString());
        createReq.put("endTime", LocalDateTime.now().plusDays(2).toString());
        createReq.put("description", "请按时完成");
        ArrayNode problemIds = createReq.putArray("problemIds");
        problemIds.add(problemA);
        problemIds.add(problemB);

        JsonNode created = readJson(authed(post("/api/homeworks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0))));
        long homeworkId = created.get("data").get("id").asLong();

        // 学生查看列表
        authed(get("/api/homeworks").param("classId", String.valueOf(classId)), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].title", is("第一次作业")));

        // 查询题目
        authed(get("/api/homeworks/{id}/problems", homeworkId), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(2)));

        // 学生无法新增题目
        ObjectNode addReq = objectMapper.createObjectNode();
        addReq.putArray("problemIds").add(problemC);
        authed(post("/api/homeworks/{id}/problems", homeworkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)), student.token())
                .andExpect(status().isForbidden());

        // 教师新增题目
        authed(post("/api/homeworks/{id}/problems", homeworkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)), teacher.token())
                .andExpect(status().isOk());

        // 更新作业并替换题目列表
        ObjectNode updateReq = objectMapper.createObjectNode();
        updateReq.put("title", "第一次作业-更新");
        ArrayNode newProblems = updateReq.putArray("problemIds");
        newProblems.add(problemB);
        newProblems.add(problemC);
        authed(put("/api/homeworks/{id}", homeworkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("第一次作业-更新")));

        authed(get("/api/homeworks/{id}/problems", homeworkId), teacher.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", equalTo(2)));

        // 删除作业题目
        authed(delete("/api/homeworks/{id}/problems/{problemId}", homeworkId, problemB), teacher.token())
                .andExpect(status().isOk());

        // 删除作业
        authed(delete("/api/homeworks/{id}", homeworkId), teacher.token())
                .andExpect(status().isOk());

        authed(get("/api/homeworks/{id}", homeworkId), student.token())
                .andExpect(status().isNotFound());
    }

    private Long createClassForTeacher(Long teacherId) {
        Classes classes = new Classes();
        classes.setName("Class-" + uniqueLabel("hw"));
        classes.setCreatorId(teacherId);
        classes.setCode(uniqueLabel("code"));
        classesService.save(classes);
        Assertions.assertThat(classes.getId()).isNotNull();
        return classes.getId();
    }

    private Long createProblemFixture(String prefix) {
        Problem problem = new Problem();
        problem.setName(prefix + "-" + uniqueLabel("prob"));
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
        return problem.getId();
    }
}
