package com.padelplay.server.facade;

import com.padelplay.common.dto.ParticipantePendienteReporteDto;
import com.padelplay.common.dto.ReporteExperienciaDto;
import com.padelplay.common.dto.ReporteExperienciaRequestDto;
import com.padelplay.server.service.JwtService;
import com.padelplay.server.service.ReporteExperienciaService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/partidos/{partidoId}/reportes-experiencia")
@CrossOrigin(origins = "*")
public class ReporteExperienciaController {

    private final ReporteExperienciaService reporteExperienciaService;
    private final JwtService jwtService;

    public ReporteExperienciaController(ReporteExperienciaService reporteExperienciaService,
                                        JwtService jwtService) {
        this.reporteExperienciaService = reporteExperienciaService;
        this.jwtService = jwtService;
    }

    @GetMapping("/pendientes")
    public ResponseEntity<?> listarPendientes(@PathVariable Long partidoId,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            List<ParticipantePendienteReporteDto> pendientes =
                    reporteExperienciaService.listarParticipantesPendientes(partidoId, usuarioId);
            return ResponseEntity.ok(pendientes);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crearReporte(@PathVariable Long partidoId,
                                          @RequestHeader("Authorization") String authHeader,
                                          @RequestBody ReporteExperienciaRequestDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            ReporteExperienciaDto reporte = reporteExperienciaService.crearReporte(partidoId, usuarioId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(reporte);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
