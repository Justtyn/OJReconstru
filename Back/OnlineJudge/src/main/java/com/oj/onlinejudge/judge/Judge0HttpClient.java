package com.oj.onlinejudge.judge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oj.onlinejudge.config.Judge0Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class Judge0HttpClient implements Judge0Client {

    private final RestTemplate restTemplate;
    private final Judge0Properties properties;

    @Override
    public Judge0Result execute(Judge0Request request) {
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .path("/submissions")
                .queryParam("base64_encoded", false)
                .queryParam("wait", true)
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("source_code", request.getSourceCode());
        body.put("language_id", request.getLanguageId());
        body.put("stdin", request.getStdin());
        body.put("expected_output", request.getExpectedOutput());
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        Judge0SubmissionResponse response = restTemplate.postForObject(uri, httpEntity, Judge0SubmissionResponse.class);
        if (response == null) {
            throw new IllegalStateException("Judge0 响应为空");
        }
        return new Judge0Result(
                response.getToken(),
                response.getStatus() != null ? response.getStatus().getId() : null,
                response.getStatus() != null ? response.getStatus().getDescription() : null,
                response.getStdout(),
                response.getStderr(),
                response.getCompileOutput(),
                response.getMessage(),
                response.getTime(),
                response.getMemory()
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Judge0SubmissionResponse {
        private String token;
        private Judge0Status status;
        private String stdout;
        private String stderr;
        @JsonProperty("compile_output")
        private String compileOutput;
        private String message;
        private Double time;
        private Integer memory;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Judge0Status getStatus() {
            return status;
        }

        public void setStatus(Judge0Status status) {
            this.status = status;
        }

        public String getStdout() {
            return stdout;
        }

        public void setStdout(String stdout) {
            this.stdout = stdout;
        }

        public String getStderr() {
            return stderr;
        }

        public void setStderr(String stderr) {
            this.stderr = stderr;
        }

        public String getCompileOutput() {
            return compileOutput;
        }

        public void setCompileOutput(String compileOutput) {
            this.compileOutput = compileOutput;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Double getTime() {
            return time;
        }

        public void setTime(Double time) {
            this.time = time;
        }

        public Integer getMemory() {
            return memory;
        }

        public void setMemory(Integer memory) {
            this.memory = memory;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Judge0Status {
        private Integer id;
        private String description;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
