package com.oj.onlinejudge.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JacksonConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeLongIdAsString() throws Exception {
        MockPayload payload = new MockPayload();
        payload.setId(1988870655198408289L);

        String json = objectMapper.writeValueAsString(payload);

        assertThat(json).contains("\"id\":\"1988870655198408289\"");
    }

    @Test
    void shouldKeepSafeLongAsNumber() throws Exception {
        MockPayload payload = new MockPayload();
        payload.setId(12345L);

        String json = objectMapper.writeValueAsString(payload);

        assertThat(json).contains("\"id\":12345");
    }

    static class MockPayload {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
