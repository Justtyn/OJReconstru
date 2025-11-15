package com.oj.onlinejudge.service;

import java.util.Optional;

public interface VerificationCodeService {
    void savePending(String username, String email, String passwordHash, String name, String code, long expireMinutes);

    Optional<Pending> get(String username);

    void increaseAttempts(String username);

    void remove(String username);

    class Pending {
        public String username;
        public String email;
        public String passwordHash;
        public String name;
        public String code;
        public int attempts;
        public long expireEpochMillis;
    }
}

