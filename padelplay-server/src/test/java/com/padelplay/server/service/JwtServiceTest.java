package com.padelplay.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        Field secretField = JwtService.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtService, "testsecretkeytestsecretkeytestsecret");

        Field expirationField = JwtService.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 86400000L);
    }

    @Test
    void extraerUsuarioId_shouldReturnLong_whenClaimIsNumber() {
        String token = jwtService.generarToken(123L, "user@example.com");
        assertNotNull(token);

        Long userId = jwtService.extraerUsuarioId(token);

        assertEquals(123L, userId);
    }
}
