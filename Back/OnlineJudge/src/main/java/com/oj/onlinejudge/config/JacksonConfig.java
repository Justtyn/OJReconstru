package com.oj.onlinejudge.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 全局 Jackson 配置：对超过 JS 安全整数范围的 Long 统一序列化为字符串，避免前端 JSON 精度丢失。
 */
@Configuration
public class JacksonConfig {

    private static final long JS_MAX_SAFE_INTEGER = 9007199254740991L;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        JsonSerializer<Long> serializer = new JsonSerializer<Long>() {
            @Override
            public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                    return;
                }
                long abs = Math.abs(value);
                if (abs > JS_MAX_SAFE_INTEGER) {
                    gen.writeString(Long.toString(value));
                } else {
                    gen.writeNumber(value);
                }
            }
        };
        return builder -> {
            builder.serializerByType(Long.class, serializer);
            builder.serializerByType(Long.TYPE, serializer);
        };
    }
}
