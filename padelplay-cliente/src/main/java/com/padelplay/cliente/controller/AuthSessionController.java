package com.padelplay.cliente.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para gestionar la sesión de autenticación en el cliente.
 */
@RestController
@RequestMapping("/auth")
public class AuthSessionController {

    /**
     * Establece el token JWT en la sesión del servidor.
     */
    @PostMapping("/establecer-sesion")
    public ResponseEntity<?> establecerSesion(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        
        String token = request.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token requerido"));
        }
        
        session.setAttribute("token", token);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * Cierra la sesión del usuario.
     */
    @PostMapping("/cerrar-sesion")
    public ResponseEntity<?> cerrarSesion(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * Verifica si hay una sesión activa.
     */
    @GetMapping("/verificar-sesion")
    public ResponseEntity<?> verificarSesion(HttpSession session) {
        String token = (String) session.getAttribute("token");
        boolean activa = token != null && !token.isBlank();
        return ResponseEntity.ok(Map.of("activa", activa));
    }
}
