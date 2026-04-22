package com.padelplay.server.facade;

import com.padelplay.common.dto.AmigoPerfilDto;
import com.padelplay.server.service.AmigosService;
import com.padelplay.server.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/amigos")
@CrossOrigin(origins = "*")
public class AmigosController {

    private final AmigosService amigosService;
    private final JwtService jwtService;

    public AmigosController(AmigosService amigosService, JwtService jwtService) {
        this.amigosService = amigosService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> listarAmigos(@RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            List<AmigoPerfilDto> amigos = amigosService.listarAmigos(usuarioId);
            return ResponseEntity.ok(amigos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{usuarioObjetivoId}")
    public ResponseEntity<?> obtenerAmigo(@RequestHeader("Authorization") String authHeader,
                                          @PathVariable Long usuarioObjetivoId) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            AmigoPerfilDto amigo = amigosService.obtenerAmigo(usuarioId, usuarioObjetivoId);
            return ResponseEntity.ok(amigo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{usuarioObjetivoId}/seguir")
    public ResponseEntity<?> seguir(@RequestHeader("Authorization") String authHeader,
                                    @PathVariable Long usuarioObjetivoId) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            amigosService.seguir(usuarioId, usuarioObjetivoId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (IllegalArgumentException e) {
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
            throw new RuntimeException("Token inválido o expirado");
        }
        return jwtService.extraerUsuarioId(token);
    }
}