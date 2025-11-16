package com.oj.onlinejudge.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oj.onlinejudge.domain.entity.Classes;
import com.oj.onlinejudge.domain.entity.Homework;
import com.oj.onlinejudge.domain.entity.HomeworkProblem;
import com.oj.onlinejudge.domain.entity.Problem;
import com.oj.onlinejudge.domain.entity.ProblemTestcase;
import com.oj.onlinejudge.service.ClassesService;
import com.oj.onlinejudge.service.HomeworkProblemService;
import com.oj.onlinejudge.service.HomeworkService;
import com.oj.onlinejudge.service.ProblemService;
import com.oj.onlinejudge.service.ProblemTestcaseService;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class SubmissionControllerTest extends ControllerTestSupport {

    @Autowired
    private ProblemService problemService;
    @Autowired
    private ProblemTestcaseService problemTestcaseService;
    @Autowired
    private HomeworkService homeworkService;
    @Autowired
    private HomeworkProblemService homeworkProblemService;
    @Autowired
    private ClassesService classesService;

    @Test
    @DisplayName("学生提交代码并查询提交记录")
    void submitAndList() throws Exception {
        Problem problem = createProblem();
        Long tc1 = createTestcase(problem.getId(), "1 2", "3");
        Long tc2 = createTestcase(problem.getId(), "2 3", "5");
        Homework homework = createHomework(problem.getId());

        TestUser student = registerStudent();

        ObjectNode req = objectMapper.createObjectNode();
        req.put("problemId", problem.getId());
        req.put("homeworkId", homework.getId());
        req.put("languageId", 71);
        req.put("sourceCode", "print(int(input().split()[0]) + int(input().split()[0]) if False else 5)");
        // keep source simple: stub doesn't parse input; ensure accepted by not containing "fail"
        JsonNode created = readJson(authed(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)), student.token())
                .andExpect(status().isOk()));
        Assertions.assertThat(created.get("data").get("problemId").asLong()).isEqualTo(problem.getId());
        long submissionId = created.get("data").get("id").asLong();

        authed(get("/api/submissions/{id}", submissionId), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.testcaseResults.length()", is(2)));

        authed(get("/api/submissions").param("problemId", String.valueOf(problem.getId())), student.token())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(1)));
    }

    private Problem createProblem() {
        Problem problem = new Problem();
        problem.setName("Sub-" + uniqueLabel("prob"));
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
        return problem;
    }

    private Long createTestcase(Long problemId, String input, String output) {
        ProblemTestcase testcase = new ProblemTestcase();
        testcase.setProblemId(problemId);
        testcase.setInputData(input);
        testcase.setOutputData(output);
        problemTestcaseService.save(testcase);
        return testcase.getId();
    }

    private Homework createHomework(Long problemId) {
        Homework homework = new Homework();
        homework.setTitle("HW-" + uniqueLabel("hw"));
        homework.setClassId(createClass());
        homework.setStartTime(LocalDateTime.now().minusDays(1));
        homework.setEndTime(LocalDateTime.now().plusDays(1));
        homework.setDescription("desc");
        homework.setIsActive(true);
        homeworkService.save(homework);
        HomeworkProblem hp = new HomeworkProblem();
        hp.setHomeworkId(homework.getId());
        hp.setProblemId(problemId);
        homeworkProblemService.save(hp);
        return homework;
    }

    private Long createClass() {
        Classes clazz = new Classes();
        clazz.setName("Class-" + uniqueLabel("cls"));
        clazz.setCode(uniqueLabel("code"));
        classesService.save(clazz);
        return clazz.getId();
    }
}
