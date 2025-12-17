package com.oj.onlinejudge.judge;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class Judge0ClientStub implements Judge0Client {

    private final Map<String, Judge0Result> store = new ConcurrentHashMap<>();

    @Override
    public Judge0Result submit(Judge0Request request) {
        boolean accepted = request.getSourceCode() != null && !request.getSourceCode().contains("fail");
        int statusId = accepted ? 3 : 4;
        String desc = accepted ? "Accepted (stub)" : "Wrong Answer (stub)";
        String token = "stub-" + UUID.randomUUID();
        Judge0Result result = new Judge0Result(token, statusId, desc,
                request.getStdin(), "", null, null, accepted ? 0.1 : 0.2, accepted ? 1024 : 2048);
        store.put(token, result);
        return result;
    }

    @Override
    public Judge0Result get(String token) {
        Judge0Result result = store.get(token);
        if (result == null) {
            return new Judge0Result(token, 13, "Internal Error (stub)",
                    null, null, null, "stub token not found", null, null);
        }
        return result;
    }
}
