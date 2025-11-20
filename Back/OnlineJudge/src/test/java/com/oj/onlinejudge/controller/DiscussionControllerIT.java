package com.oj.onlinejudge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oj.onlinejudge.domain.dto.DiscussionCommentRequest;
import com.oj.onlinejudge.domain.dto.DiscussionRequest;
import com.oj.onlinejudge.domain.entity.Discussion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

@DisplayName("DiscussionController 集成测试")
class DiscussionControllerIT extends ControllerTestSupport {

    @Test
    @DisplayName("未登录访问讨论列表返回 401")
    void listRequiresLogin() throws Exception {
        assertUnauthorized(performGet("/api/discussions", null, null));
    }

    @Test
    @DisplayName("学生创建讨论并可更新自己的内容，其他学生无权")
    void discussionCrudWithComments() throws Exception {
        AuthSession student = registerAndLoginStudent();
        DiscussionRequest create = new DiscussionRequest();
        create.setTitle("Question about problem");
        create.setContent("How can I optimize the solution?");
        Discussion discussion =
                readData(performPostJson("/api/discussions", create, student.token()), Discussion.class);

        DiscussionRequest updateOther = new DiscussionRequest();
        updateOther.setContent("Malicious edit");
        AuthSession otherStudent = registerAndLoginStudent();
        assertForbidden(performPutJson("/api/discussions/" + discussion.getId(), updateOther, otherStudent.token()));

        DiscussionRequest updateSelf = new DiscussionRequest();
        updateSelf.setContent("Additional clarification");
        Discussion updated = readData(
                performPutJson("/api/discussions/" + discussion.getId(), updateSelf, student.token()), Discussion.class);
        assertThat(updated.getContent()).contains("Additional");

        // 评论
        DiscussionCommentRequest commentReq = new DiscussionCommentRequest();
        commentReq.setContent("I can help answer this.");
        JsonNode comment = assertOkAndStandardApiResponse(
                performPostJson("/api/discussions/" + discussion.getId() + "/comments", commentReq, otherStudent.token()))
                .get("data");
        long commentId = comment.get("id").asLong();

        // 其他学生删除评论失败，管理员成功
        assertForbidden(performDelete("/api/discussions/comments/" + commentId, null, student.token()));
        AuthSession admin = registerAndLoginAdmin();
        assertOkAndStandardApiResponse(performDelete("/api/discussions/comments/" + commentId, null, admin.token()));
    }

    @Test
    @DisplayName("管理员可以删除任意讨论")
    void adminDeleteDiscussion() throws Exception {
        AuthSession student = registerAndLoginStudent();
        DiscussionRequest create = new DiscussionRequest();
        create.setTitle("Bug discussion");
        create.setContent("Please fix it.");
        Discussion discussion =
                readData(performPostJson("/api/discussions", create, student.token()), Discussion.class);

        AuthSession admin = registerAndLoginAdmin();
        assertOkAndStandardApiResponse(performDelete("/api/discussions/" + discussion.getId(), null, admin.token()));
    }
}
