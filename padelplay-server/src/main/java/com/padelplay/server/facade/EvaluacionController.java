package com.padelplay.server.facade;

import com.padelplay.common.dto.CrearSolicitudEvaluacionDto;
import com.padelplay.common.dto.ResponderSolicitudEvaluacionDto;
import com.padelplay.common.dto.SolicitudEvaluacionDto;
import com.padelplay.server.service.EvaluacionService;
import com.padelplay.server.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin(origins = "*")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;
    private final JwtService jwtService;

    public EvaluacionController(EvaluacionService evaluacionService, JwtService jwtService) {
        this.evaluacionService = evaluacionService;
        this.jwtService = jwtService;
    }

    @GetMapping("/entrenadores")
    public ResponseEntity<?> obtenerEntrenadoresDisponibles() {
        try {
            return ResponseEntity.ok(evaluacionService.obtenerEntrenadoresDisponibles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<?> crearSolicitud(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody CrearSolicitudEvaluacionDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            SolicitudEvaluacionDto solicitud = evaluacionService.crearSolicitud(usuarioId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(solicitud);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<?> obtenerMisSolicitudes(@RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            return ResponseEntity.ok(evaluacionService.obtenerMisSolicitudes(usuarioId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/recibidas")
    public ResponseEntity<?> obtenerSolicitudesRecibidas(@RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            return ResponseEntity.ok(evaluacionService.obtenerSolicitudesRecibidas(usuarioId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/solicitudes/{id}/aceptar")
    public ResponseEntity<?> aceptarSolicitud(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable Long id,
                                              @RequestBody(required = false) ResponderSolicitudEvaluacionDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            return ResponseEntity.ok(evaluacionService.aceptarSolicitud(id, usuarioId, request));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable Long id,
                                               @RequestBody(required = false) ResponderSolicitudEvaluacionDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            return ResponseEntity.ok(evaluacionService.rechazarSolicitud(id, usuarioId, request));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private Long extraerUsuarioId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }

        String token = authHeader.substring(7);
        if (!jwtService.validarToken(token)) {
            throw new RuntimeException("Token invalido o expirado");
        }

        return jwtService.extraerUsuarioId(token);
    }
}
