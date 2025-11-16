package com.oj.onlinejudge.judge;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class Judge0ClientStub implements Judge0Client {

    @Override
    public Judge0Result execute(Judge0Request request) {
        boolean accepted = request.getSourceCode() != null && !request.getSourceCode().contains("fail");
        int statusId = accepted ? 3 : 4;
        String desc = accepted ? "Accepted (stub)" : "Wrong Answer (stub)";
        return new Judge0Result("stub-token", statusId, desc,
                request.getStdin(), "", null, null, accepted ? 0.1 : 0.2, accepted ? 1024 : 2048);
    }
}
