package com.padelplay.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class TrayectoriaCursorService {

    private final String secret;

    public TrayectoriaCursorService(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String codificar(LocalDateTime fechaHora, Long partidoId) {
        String payload = fechaHora.toString() + "|" + partidoId;
        String signed = payload + "|" + firmar(payload);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signed.getBytes(StandardCharsets.UTF_8));
    }

    public TrayectoriaCursor decodificar(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|", 3);
            if (parts.length != 3) {
                throw new IllegalArgumentException("Cursor invalido.");
            }

            String payload = parts[0] + "|" + parts[1];
            if (!firmar(payload).equals(parts[2])) {
                throw new IllegalArgumentException("Cursor invalido.");
            }

            return new TrayectoriaCursor(LocalDateTime.parse(parts[0]), Long.parseLong(parts[1]));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cursor invalido.");
        } catch (Exception e) {
            throw new IllegalArgumentException("Cursor invalido.");
        }
    }

    private String firmar(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((payload + "|" + secret).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el cursor.");
        }
    }

    public record TrayectoriaCursor(LocalDateTime fechaHora, Long partidoId) {
    }
}
