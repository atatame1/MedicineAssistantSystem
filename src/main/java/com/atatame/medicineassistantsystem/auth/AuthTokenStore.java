package com.atatame.medicineassistantsystem.auth;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthTokenStore {

    public static class TokenInfo {
        private final Long userId;
        private final Instant issuedAt;

        public TokenInfo(Long userId, Instant issuedAt) {
            this.userId = userId;
            this.issuedAt = issuedAt;
        }

        public Long getUserId() {
            return userId;
        }

        public Instant getIssuedAt() {
            return issuedAt;
        }
    }

    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    public String issue(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, new TokenInfo(userId, Instant.now()));
        return token;
    }

    public TokenInfo get(String token) {
        if (token == null || token.isBlank()) return null;
        return tokens.get(token);
    }

    public void revoke(String token) {
        if (token == null || token.isBlank()) return;
        tokens.remove(token);
    }
}

