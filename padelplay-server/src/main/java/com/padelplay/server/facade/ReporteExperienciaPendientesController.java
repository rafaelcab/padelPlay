package com.padelplay.server.facade;

import com.padelplay.common.dto.PartidoPendienteReporteDto;
import com.padelplay.server.service.JwtService;
import com.padelplay.server.service.ReporteExperienciaService;
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
@RequestMapping("/api/reportes-experiencia")
@CrossOrigin(origins = "*")
public class ReporteExperienciaPendientesController {

    private final ReporteExperienciaService reporteExperienciaService;
    private final JwtService jwtService;

    public ReporteExperienciaPendientesController(ReporteExperienciaService reporteExperienciaService,
                                                  JwtService jwtService) {
        this.reporteExperienciaService = reporteExperienciaService;
        this.jwtService = jwtService;
    }

    @GetMapping("/partidos-pendientes")
    public ResponseEntity<?> listarPartidosPendientes(@RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            List<PartidoPendienteReporteDto> partidos =
                    reporteExperienciaService.listarPartidosPendientesDeReportar(usuarioId);
            return ResponseEntity.ok(partidos);
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
