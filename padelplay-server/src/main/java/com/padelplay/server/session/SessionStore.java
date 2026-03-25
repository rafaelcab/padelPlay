package com.padelplay.server.session;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStore {

    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public String createSession(SessionData sessionData) {
        String token = generateToken();
        sessionData.setCreatedAt(Instant.now());
        sessionData.setExpiresAt(Instant.now().plusSeconds(60 * 60 * 24));
        sessions.put(token, sessionData);
        return token;
    }

    public SessionData getSession(String token) {
        if (token == null) {
            return null;
        }

        SessionData data = sessions.get(token);
        if (data == null) {
            return null;
        }

        if (data.getExpiresAt().isBefore(Instant.now())) {
            sessions.remove(token);
            return null;
        }

        return data;
    }

    public void removeSession(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}