package com.padelplay.cliente.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesController {

    private final RestTemplate restTemplate;
    private final String serverApiUrl;

    public NotificacionesController(@Value("${server.api.url:http://localhost:8080}") String serverApiUrl) {
        this.restTemplate = new RestTemplate();
        this.serverApiUrl = serverApiUrl;
    }

    @GetMapping("/recordatorios")
    public ResponseEntity<?> obtenerRecordatorios(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Sesion no activa"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    serverApiUrl + "/api/notificaciones/recordatorios",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );
            return ResponseEntity.status(response.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "No se pudieron cargar las notificaciones"));
        }
    }
}