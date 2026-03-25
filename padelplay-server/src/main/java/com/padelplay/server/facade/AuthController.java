package com.padelplay.server.facade;

import com.padelplay.common.dto.AuthResponseDto;
import com.padelplay.common.dto.GoogleAuthRequestDto;
import com.padelplay.server.entity.AuthProvider;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.UsuarioRepository;
import com.padelplay.server.service.GoogleAuthService;
import com.padelplay.server.service.GoogleAuthService.GoogleUserInfo;
import com.padelplay.server.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Controller para autenticación (Google OAuth y registro/login local).
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Ajustar en producción
public class AuthController {

    private final GoogleAuthService googleAuthService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(GoogleAuthService googleAuthService,
                          JwtService jwtService,
                          UsuarioRepository usuarioRepository) {
        this.googleAuthService = googleAuthService;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * DTO para registro manual.
     */
    public record RegistroRequest(String nombre, String email, String password) {}

    /**
     * DTO para login manual.
     */
    public record LoginRequest(String email, String password) {}

    /**
     * Endpoint para registro manual con email y contraseña.
     * Crea un nuevo usuario y devuelve un JWT.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
        
        // Validaciones
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

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El email ya está registrado"));
        }

        // Crear nuevo usuario con AuthProvider.LOCAL
        Usuario usuario = new Usuario(request.nombre(), request.email(), request.password());
        usuario.setAuthProvider(AuthProvider.LOCAL);
        usuarioRepository.save(usuario);

        // Generar JWT
        String token = jwtService.generarToken(usuario.getId(), usuario.getEmail());

        AuthResponseDto response = new AuthResponseDto(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                null, // No hay foto en registro manual
                true  // Siempre es nuevo usuario en registro
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para login manual con email y contraseña.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest request) {
        
        if (request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email es requerido"));
        }
        
        if (request.password() == null || request.password().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña es requerida"));
        }

        // Buscar usuario por email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.email());
        
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email o contraseña incorrectos"));
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar contraseña (en producción usar BCrypt)
        if (usuario.getPassword() == null || !usuario.getPassword().equals(request.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email o contraseña incorrectos"));
        }

        // Generar JWT
        String token = jwtService.generarToken(usuario.getId(), usuario.getEmail());

        // Determinar si necesita seleccionar perfil
        boolean necesitaSeleccionarPerfil = usuario.requiereSeleccionPerfil();

        AuthResponseDto response = new AuthResponseDto(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getPictureUrl(),
                necesitaSeleccionarPerfil // Si no tiene perfil, es como si fuera nuevo
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para autenticación con Google.
     * Recibe el ID Token de Google y devuelve un JWT propio.
     * 
     * Lógica de upsert:
     * - Si el email no existe: crea usuario nuevo
     * - Si existe con Google: login directo
     * - Si existe con password: vincula identidad de Google (colisión de cuentas)
     */
    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequestDto request) {
        
        if (request.getCredential() == null || request.getCredential().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token de Google requerido"));
        }

        // Verificar token con Google
        GoogleUserInfo googleUser = googleAuthService.verificarToken(request.getCredential());

        if (googleUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token de Google inválido o expirado"));
        }

        if (!googleUser.emailVerificado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "El email de Google no está verificado"));
        }

        // Buscar usuario existente por email
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(googleUser.email());
        boolean nuevoUsuario = false;
        Usuario usuario;

        if (usuarioExistente.isPresent()) {
            usuario = usuarioExistente.get();

            // Caso: usuario existe pero se registró con password (colisión de cuentas)
            // Vinculamos la identidad de Google para permitir ambos métodos de login
            if (usuario.getGoogleId() == null) {
                usuario.vincularGoogle(googleUser.googleId(), googleUser.pictureUrl());
                usuarioRepository.save(usuario);
            }
            
            // Verificar si necesita seleccionar perfil
            nuevoUsuario = usuario.requiereSeleccionPerfil();

        } else {
            // Caso: nuevo usuario - registro silencioso
            usuario = new Usuario(
                    googleUser.email(),
                    googleUser.nombre(),
                    googleUser.pictureUrl(),
                    googleUser.googleId(),
                    AuthProvider.GOOGLE
            );
            usuarioRepository.save(usuario);
            nuevoUsuario = true;
        }

        // Generar JWT propio de la aplicación
        String token = jwtService.generarToken(usuario.getId(), usuario.getEmail());

        AuthResponseDto response = new AuthResponseDto(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getPictureUrl(),
                nuevoUsuario
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para verificar si un token JWT es válido.
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token no proporcionado"));
        }

        String token = authHeader.substring(7);

        if (jwtService.validarToken(token)) {
            String email = jwtService.extraerEmail(token);
            return ResponseEntity.ok(Map.of("valid", true, "email", email));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false, "error", "Token inválido o expirado"));
    }
}
