package com.padelplay.server.facade;

import com.padelplay.common.dto.AuthResponseDto;
import com.padelplay.common.dto.GoogleAuthRequestDto;
import com.padelplay.common.dto.RegistroRequestDto;
import com.padelplay.server.entity.AuthProvider;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.UsuarioRepository;
import com.padelplay.server.service.GoogleAuthService;
import com.padelplay.server.service.GoogleAuthService.GoogleUserInfo;
import com.padelplay.server.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Controller para autenticación (Google OAuth y futura autenticación local).
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Ajustar en producción
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final int PASSWORD_SALT_BYTES = 16;
    private static final int PASSWORD_ITERATIONS = 65536;
    private static final int PASSWORD_KEY_LENGTH = 256;

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
     * Endpoint para registro manual con email y password.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequestDto request) {
        if (request == null
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getNombre() == null || request.getNombre().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Nombre, email y contraseña son obligatorios"));
        }
        if (request.getPassword().length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe tener al menos 8 caracteres"));
        }

        String email = request.getEmail().trim().toLowerCase();
        String nombre = request.getNombre().trim();

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();

            if (usuario.getPassword() == null) {
                String passwordHashed = hashPassword(request.getPassword());
                usuario.setPassword(passwordHashed);
                if (usuario.getAuthProvider() == AuthProvider.GOOGLE) {
                    usuario.setAuthProvider(AuthProvider.MIXED);
                }
                usuarioRepository.save(usuario);

                String token = jwtService.generarToken(usuario.getId(), usuario.getEmail());
                AuthResponseDto response = new AuthResponseDto(
                        token,
                        usuario.getEmail(),
                        usuario.getNombre(),
                        usuario.getPictureUrl(),
                        false
                );
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email ya registrado"));
        }

        String passwordHashed = hashPassword(request.getPassword());
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setNombre(nombre);
        usuario.setPassword(passwordHashed);
        usuario.setAuthProvider(AuthProvider.LOCAL);
        usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuario.getId(), usuario.getEmail());
        AuthResponseDto response = new AuthResponseDto(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getPictureUrl(),
                true
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

    private String hashPassword(String password) {
        try {
            byte[] salt = new byte[PASSWORD_SALT_BYTES];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PASSWORD_ITERATIONS, PASSWORD_KEY_LENGTH);
            try {
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                byte[] hashed = factory.generateSecret(spec).getEncoded();
                return PASSWORD_ITERATIONS + ":"
                        + Base64.getEncoder().encodeToString(salt)
                        + ":" + Base64.getEncoder().encodeToString(hashed);
            } finally {
                spec.clearPassword();
            }
        } catch (Exception e) {
            logger.error("Error al procesar la contraseña", e);
            throw new RuntimeException("Error al generar hash de contraseña", e);
        }
    }
}
