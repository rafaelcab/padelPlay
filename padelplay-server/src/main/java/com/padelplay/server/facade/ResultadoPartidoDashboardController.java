package com.padelplay.server.facade;

import com.padelplay.common.dto.ResultadoPartidoGestionCreadorDto;
import com.padelplay.common.dto.ResultadoPartidoPendienteValidacionDto;
import com.padelplay.server.service.JwtService;
import com.padelplay.server.service.ResultadoPartidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resultados-partido")
@CrossOrigin(origins = "*")
public class ResultadoPartidoDashboardController {

    private final ResultadoPartidoService resultadoPartidoService;
    private final JwtService jwtService;

    public ResultadoPartidoDashboardController(ResultadoPartidoService resultadoPartidoService, JwtService jwtService) {
        this.resultadoPartidoService = resultadoPartidoService;
        this.jwtService = jwtService;
    }

    @GetMapping("/pendientes-validacion")
    public ResponseEntity<?> listarPendientesValidacion(@RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            List<ResultadoPartidoPendienteValidacionDto> pendientes =
                    resultadoPartidoService.listarPendientesValidacion(usuarioId);
            return ResponseEntity.ok(pendientes);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/mis-registros")
    public ResponseEntity<?> listarResultadosGestionCreador(@RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            List<ResultadoPartidoGestionCreadorDto> resultados =
                    resultadoPartidoService.listarResultadosGestionCreador(usuarioId);
            return ResponseEntity.ok(resultados);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
