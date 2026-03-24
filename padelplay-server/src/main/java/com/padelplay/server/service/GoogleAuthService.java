package com.padelplay.server.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio para verificar ID Tokens de Google OAuth.
 */
@Service
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String googleClientId;

    private GoogleIdTokenVerifier verifier;

    /**
     * Verifica el ID Token de Google y extrae la información del usuario.
     * 
     * @param idTokenString El token JWT recibido desde el frontend
     * @return GoogleUserInfo con los datos del usuario, o null si el token es inválido
     */
    public GoogleUserInfo verificarToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier tokenVerifier = getVerifier();
            GoogleIdToken idToken = tokenVerifier.verify(idTokenString);

            if (idToken == null) {
                return null;
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            // Validar que el audience coincida con nuestro Client ID
            if (!payload.getAudience().equals(googleClientId)) {
                return null;
            }

            return new GoogleUserInfo(
                    payload.getSubject(),                           // Google ID único
                    payload.getEmail(),
                    (String) payload.get("name"),
                    (String) payload.get("picture"),
                    payload.getEmailVerified()
            );

        } catch (Exception e) {
            return null;
        }
    }

    private GoogleIdTokenVerifier getVerifier() {
        if (verifier == null) {
            verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
        }
        return verifier;
    }

    /**
     * Record con la información del usuario extraída del token de Google.
     */
    public record GoogleUserInfo(
            String googleId,
            String email,
            String nombre,
            String pictureUrl,
            boolean emailVerificado
    ) {}
}
