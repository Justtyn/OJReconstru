package com.oj.onlinejudge.service.impl;

import com.oj.onlinejudge.service.VerificationCodeService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.auth.verify.store", havingValue = "memory", matchIfMissing = true)
public class VerificationCodeServiceMemoryImpl implements VerificationCodeService {

    private final Map<String, Pending> store = new ConcurrentHashMap<>();

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
        store.put(username, p);
    }

    @Override
    public Optional<Pending> get(String username) {
        Pending p = store.get(username);
        return Optional.ofNullable(p);
    }

    @Override
    public void increaseAttempts(String username) {
        Pending p = store.get(username);
        if (p != null) p.attempts++;
    }

    @Override
    public void remove(String username) {
        store.remove(username);
    }
}

