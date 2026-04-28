package com.padelplay.server.facade;

import com.padelplay.common.dto.RegistrarResultadoPartidoRequestDto;
import com.padelplay.common.dto.ResultadoPartidoDto;
import com.padelplay.common.dto.ValidarResultadoPartidoRequestDto;
import com.padelplay.server.service.JwtService;
import com.padelplay.server.service.ResultadoPartidoService;
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

@RestController
@RequestMapping("/api/partidos/{partidoId}/resultado")
@CrossOrigin(origins = "*")
public class ResultadoPartidoController {

    private final ResultadoPartidoService resultadoPartidoService;
    private final JwtService jwtService;

    public ResultadoPartidoController(ResultadoPartidoService resultadoPartidoService, JwtService jwtService) {
        this.resultadoPartidoService = resultadoPartidoService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> registrarResultado(@PathVariable Long partidoId,
                                                @RequestHeader("Authorization") String authHeader,
                                                @RequestBody RegistrarResultadoPartidoRequestDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            ResultadoPartidoDto resultado = resultadoPartidoService.registrarResultado(partidoId, usuarioId, request);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/validaciones")
    public ResponseEntity<?> validarResultado(@PathVariable Long partidoId,
                                              @RequestHeader("Authorization") String authHeader,
                                              @RequestBody ValidarResultadoPartidoRequestDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            ResultadoPartidoDto resultado = resultadoPartidoService.validarResultado(partidoId, usuarioId, request);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerResultado(@PathVariable Long partidoId,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            ResultadoPartidoDto resultado = resultadoPartidoService.obtenerResultado(partidoId, usuarioId);
            return ResponseEntity.ok(resultado);
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
