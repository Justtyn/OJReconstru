package com.oj.onlinejudge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.service.VerificationCodeService;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.verify.store", havingValue = "redis")
public class VerificationCodeServiceRedisImpl implements VerificationCodeService {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @Value("${app.auth.verify-redis-prefix:verify:reg:}")
    private String prefix;

    @Override
    public void savePending(String username, String email, String passwordHash, String name, String code, long expireMinutes) {
        Pending p = new Pending();
        p.username = username;
        p.email = email;
        p.passwordHash = passwordHash;
        p.name = name;
        p.code = code;
        p.attempts = 0;
        p.expireEpochMillis = System.currentTimeMillis() + expireMinutes * 60_000L;
        try {
            String key = prefix + username;
            String json = objectMapper.writeValueAsString(p);
            redis.opsForValue().set(key, json, Duration.ofMinutes(expireMinutes));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Pending> get(String username) {
        String json = redis.opsForValue().get(prefix + username);
        if (json == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, Pending.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public void increaseAttempts(String username) {
        get(username).ifPresent(p -> {
            p.attempts++;
            try {
                String key = prefix + username;
                Long ttl = redis.getExpire(key);
                if (ttl == null || ttl <= 0) ttl = 60L; // default
                redis.opsForValue().set(key, objectMapper.writeValueAsString(p), Duration.ofSeconds(ttl));
            } catch (Exception ignored) {
            }
        });
    }

    @Override
    public void remove(String username) {
        redis.delete(prefix + username);
    }
}
