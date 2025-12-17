package com.oj.onlinejudge.judge;

public interface Judge0Client {

    /**
     * 提交代码到 Judge0。
     *
     * <p>在生产环境一般采用 wait=false，仅返回 token 与初始状态；后续需要通过 {@link #get(String)} 拉取最终结果。
     */
    Judge0Result submit(Judge0Request request);

    /**
     * 根据 token 获取 Judge0 执行结果（可能为排队/判题中/最终态）。
     */
    Judge0Result get(String token);

    /**
     * 兼容旧调用：等价于 {@link #submit(Judge0Request)}。
     */
    default Judge0Result execute(Judge0Request request) {
        return submit(request);
    }

    class Judge0Request {
        private final Integer languageId;
        private final String sourceCode;
        private final String stdin;
        private final String expectedOutput;

        public Judge0Request(Integer languageId, String sourceCode, String stdin, String expectedOutput) {
            this.languageId = languageId;
            this.sourceCode = sourceCode;
            this.stdin = stdin;
            this.expectedOutput = expectedOutput;
        }

        public Integer getLanguageId() {
            return languageId;
        }

        public String getSourceCode() {
            return sourceCode;
        }

        public String getStdin() {
            return stdin;
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }
    }

    class Judge0Result {
        private final String token;
        private final Integer statusId;
        private final String statusDescription;
        private final String stdout;
        private final String stderr;
        private final String compileOutput;
        private final String message;
        private final Double time;
        private final Integer memory;

        public Judge0Result(String token, Integer statusId, String statusDescription,
                             String stdout, String stderr, String compileOutput,
                             String message, Double time, Integer memory) {
            this.token = token;
            this.statusId = statusId;
            this.statusDescription = statusDescription;
            this.stdout = stdout;
            this.stderr = stderr;
            this.compileOutput = compileOutput;
            this.message = message;
            this.time = time;
            this.memory = memory;
        }

        public String getToken() {
            return token;
        }

        public Integer getStatusId() {
            return statusId;
        }

        public String getStatusDescription() {
            return statusDescription;
        }

        public String getStdout() {
            return stdout;
        }

        public String getStderr() {
            return stderr;
        }

        public String getCompileOutput() {
            return compileOutput;
        }

        public String getMessage() {
            return message;
        }

        public Double getTime() {
            return time;
        }

        public Integer getMemory() {
            return memory;
        }
    }
}
