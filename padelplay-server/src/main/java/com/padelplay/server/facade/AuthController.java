package com.padelplay.server.facade;

import com.padelplay.common.dto.AuthResponseDto;
import com.padelplay.common.dto.GoogleAuthRequestDto;
import com.padelplay.common.dto.LoginRequestDto;
import com.padelplay.common.dto.RegistroRequestDto;
import com.padelplay.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequestDto request) {

        if (request.nombre() == null || request.nombre().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre es requerido"));
        }

        if (request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email es requerido"));
        }

        if (request.password() == null || request.password().length() < 4) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe tener al menos 4 caracteres"));
        }

        Optional<AuthResponseDto> responseOpt = authService.register(request);

        if (responseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El email ya está registrado"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOpt.get());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequestDto request) {

        if (request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email es requerido"));
        }

        if (request.password() == null || request.password().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña es requerida"));
        }

        // Aquí llamas al login que ya tienes hecho
        Optional<AuthResponseDto> responseOpt = authService.login(request);

        if (responseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email o contraseña incorrectos"));
        }

        return ResponseEntity.ok(responseOpt.get());
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequestDto request) {

        if (request.getCredential() == null || request.getCredential().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token de Google requerido"));
        }

        AuthResponseDto response = authService.authenticateWithGoogle(request);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token de Google inválido, expirado o email no verificado"));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token no proporcionado"));
        }

        String token = authHeader.substring(7);

        Map<String, Object> result = authService.verifyToken(token);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token inválido o expirado"));
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout correcto"));
    }
}