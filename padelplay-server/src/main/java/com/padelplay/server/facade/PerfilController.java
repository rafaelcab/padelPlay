package com.padelplay.server.facade;

import com.padelplay.common.dto.CertificacionDto;
import com.padelplay.common.dto.DetallesTecnicosDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PerfilEntrenadorDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.common.dto.SeleccionRolDto;
import com.padelplay.server.entity.EspecialidadEntrenador;
import com.padelplay.server.entity.TipoCertificacion;
import com.padelplay.server.entity.TipoRol;
import com.padelplay.server.service.JwtService;
import com.padelplay.server.service.PerfilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de perfiles de usuario (Jugador/Entrenador).
 */
@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = "*")
public class PerfilController {

    private final PerfilService perfilService;
    private final JwtService jwtService;

    public PerfilController(PerfilService perfilService, JwtService jwtService) {
        this.perfilService = perfilService;
        this.jwtService = jwtService;
    }

    /**
     * Obtiene el estado actual de los perfiles del usuario.
     */
    @GetMapping("/estado")
    public ResponseEntity<?> obtenerEstadoPerfil(@RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            EstadoPerfilDto estado = perfilService.obtenerEstadoPerfil(usuarioId);
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Selecciona el rol inicial (para usuarios nuevos sin perfil).
     */
    @PostMapping("/seleccionar-rol")
    public ResponseEntity<?> seleccionarRol(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SeleccionRolDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            TipoRol rol = TipoRol.valueOf(request.getRol().toUpperCase());
            EstadoPerfilDto estado = perfilService.seleccionarRol(usuarioId, rol);
            return ResponseEntity.ok(estado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Rol inválido. Valores permitidos: JUGADOR, ENTRENADOR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cambia el rol activo del usuario (switch entre perfiles).
     */
    @PostMapping("/cambiar-rol")
    public ResponseEntity<?> cambiarRol(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SeleccionRolDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            TipoRol rol = TipoRol.valueOf(request.getRol().toUpperCase());
            EstadoPerfilDto estado = perfilService.cambiarRol(usuarioId, rol);
            return ResponseEntity.ok(estado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Rol inválido. Valores permitidos: JUGADOR, ENTRENADOR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Crea un perfil adicional para el usuario.
     */
    @PostMapping("/crear-perfil")
    public ResponseEntity<?> crearPerfilAdicional(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SeleccionRolDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            TipoRol rol = TipoRol.valueOf(request.getRol().toUpperCase());
            EstadoPerfilDto estado = perfilService.crearPerfilAdicional(usuarioId, rol);
            return ResponseEntity.ok(estado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Rol inválido. Valores permitidos: JUGADOR, ENTRENADOR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene el perfil de jugador del usuario.
     */
    @GetMapping("/jugador")
    public ResponseEntity<?> obtenerPerfilJugador(@RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            return perfilService.obtenerPerfilJugador(usuarioId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza el perfil de jugador.
     */
    @PutMapping("/jugador")
    public ResponseEntity<?> actualizarPerfilJugador(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PerfilJugadorDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            PerfilJugadorDto perfil = perfilService.actualizarPerfilJugador(usuarioId, request);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza los detalles técnicos del jugador.
     */
    @PutMapping("/jugador/detalles-tecnicos")
    public ResponseEntity<?> actualizarDetallesTecnicos(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DetallesTecnicosDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            DetallesTecnicosDto detalles = perfilService.actualizarDetallesTecnicos(usuarioId, request);
            return ResponseEntity.ok(detalles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Valor inválido en los detalles técnicos: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene las opciones disponibles para los campos del perfil técnico.
     */
    @GetMapping("/opciones-tecnicas")
    public ResponseEntity<?> obtenerOpcionesTecnicas() {
        return ResponseEntity.ok(Map.of(
            "posiciones", java.util.Arrays.stream(com.padelplay.server.entity.Posicion.values())
                    .map(p -> Map.of("valor", p.name(), "descripcion", p.getDescripcion()))
                    .toList(),
            "estilosJuego", java.util.Arrays.stream(com.padelplay.server.entity.EstiloJuego.values())
                    .map(e -> Map.of("valor", e.name(), "descripcion", e.getDescripcion()))
                    .toList(),
            "tiposGolpe", java.util.Arrays.stream(com.padelplay.server.entity.TipoGolpe.values())
                    .map(g -> Map.of("valor", g.name(), "descripcion", g.getDescripcion()))
                    .toList(),
            "manosHabiles", java.util.Arrays.stream(com.padelplay.server.entity.DetallesTecnicos.ManoHabil.values())
                    .map(m -> Map.of("valor", m.name(), "descripcion", m.getDescripcion()))
                    .toList(),
            "nivelesJuego", java.util.List.of(
                    Map.of("valor", "Principiante", "descripcion", "Principiante"),
                    Map.of("valor", "Intermedio", "descripcion", "Intermedio"),
                    Map.of("valor", "Avanzado", "descripcion", "Avanzado"),
                    Map.of("valor", "Profesional", "descripcion", "Profesional")
            )
        ));
    }

    // === ENDPOINTS PARA ENTRENADOR ===

    /**
     * Obtiene el perfil de entrenador del usuario.
     */
    @GetMapping("/entrenador")
    public ResponseEntity<?> obtenerPerfilEntrenador(@RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            return perfilService.obtenerPerfilEntrenador(usuarioId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza el perfil de entrenador.
     */
    @PutMapping("/entrenador")
    public ResponseEntity<?> actualizarPerfilEntrenador(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PerfilEntrenadorDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            PerfilEntrenadorDto perfil = perfilService.actualizarPerfilEntrenador(usuarioId, request);
            return ResponseEntity.ok(perfil);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Valor inválido: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Añade una certificación al perfil del entrenador.
     */
    @PostMapping("/entrenador/certificaciones")
    public ResponseEntity<?> agregarCertificacion(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CertificacionDto request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            PerfilEntrenadorDto perfil = perfilService.agregarCertificacion(usuarioId, request);
            return ResponseEntity.ok(perfil);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Tipo de certificación inválido: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Elimina una certificación del perfil del entrenador.
     */
    @DeleteMapping("/entrenador/certificaciones/{certificacionId}")
    public ResponseEntity<?> eliminarCertificacion(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long certificacionId) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            PerfilEntrenadorDto perfil = perfilService.eliminarCertificacion(usuarioId, certificacionId);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza todas las certificaciones del entrenador.
     */
    @PutMapping("/entrenador/certificaciones")
    public ResponseEntity<?> actualizarCertificaciones(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody List<CertificacionDto> request) {
        try {
            Long usuarioId = extraerUsuarioId(authHeader);
            PerfilEntrenadorDto perfil = perfilService.actualizarCertificaciones(usuarioId, request);
            return ResponseEntity.ok(perfil);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Tipo de certificación inválido: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene las opciones disponibles para los campos del perfil de entrenador.
     */
    @GetMapping("/opciones-entrenador")
    public ResponseEntity<?> obtenerOpcionesEntrenador() {
        return ResponseEntity.ok(Map.of(
            "tiposCertificacion", java.util.Arrays.stream(TipoCertificacion.values())
                    .map(t -> Map.of("valor", t.name(), "nombre", t.getNombre(), "descripcion", t.getDescripcion()))
                    .toList(),
            "especialidades", java.util.Arrays.stream(EspecialidadEntrenador.values())
                    .map(e -> Map.of("valor", e.name(), "nombre", e.getNombre(), "descripcion", e.getDescripcion()))
                    .toList()
        ));
    }

    // === Métodos auxiliares ===

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
