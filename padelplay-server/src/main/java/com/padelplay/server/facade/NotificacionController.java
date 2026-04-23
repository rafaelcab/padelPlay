package com.padelplay.server.facade;

import com.padelplay.server.dto.RecordatorioNotificacionDto;
import com.padelplay.server.service.JwtService;
import com.padelplay.server.service.RecordatorioPartidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final RecordatorioPartidoService recordatorioPartidoService;
    private final JwtService jwtService;

    public NotificacionController(RecordatorioPartidoService recordatorioPartidoService,
                                  JwtService jwtService) {
        this.recordatorioPartidoService = recordatorioPartidoService;
        this.jwtService = jwtService;
    }

    @GetMapping("/recordatorios")
    public ResponseEntity<?> obtenerRecordatorios(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extraerToken(authHeader);
            if (!jwtService.validarToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o expirado"));
            }

            String email = jwtService.extraerEmail(token);
            List<RecordatorioNotificacionDto> notificaciones = recordatorioPartidoService.obtenerNotificacionesRecientes(email);
            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private String extraerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token no proporcionado");
        }
        return authHeader.substring(7);
    }
}