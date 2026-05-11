package com.padelplay.server.facade;

import com.padelplay.common.dto.FeedbackPartidoDto;
import com.padelplay.common.dto.PartidoDto;
import com.padelplay.server.service.FeedbackPartidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entrenador/feedback")
@CrossOrigin(origins = "*")
@Tag(name = "Feedback Pertenador Controller", description = "Operaciones de feedback de entrenador sobre partidos de alumnos")
public class FeedbackPartidoController {

    private final FeedbackPartidoService feedbackPartidoService;

    @Autowired
    public FeedbackPartidoController(FeedbackPartidoService feedbackPartidoService) {
        this.feedbackPartidoService = feedbackPartidoService;
    }

    /**
     * Obtiene el historial de partidos para dar feedback.
     */
    @GetMapping("/historial-partidos")
    @Operation(summary = "Obtener historial de partidos", description = "Obtiene los partidos de los alumnos del entrenador para dar feedback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    })
    public ResponseEntity<List<PartidoDto>> obtenerHistorialPartidos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long entrenadorId = extraerUsuarioId(authHeader);
            List<PartidoDto> partidos = feedbackPartidoService.obtenerHistorialPartidosAlumnos(entrenadorId);
            return ResponseEntity.ok(partidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
    }

    /**
     * Guarda el feedback para un partido.
     */
    @PostMapping("/guardar")
    @Operation(summary = "Guardar feedback de partido", description = "Guarda el feedback y calificación del entrenador para un partido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback guardado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos del feedback")
    })
    public ResponseEntity<?> guardarFeedback(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> request) {
        try {
            Long entrenadorId = extraerUsuarioId(authHeader);
            Long alumnoId = Long.parseLong((String) request.get("alumnoId"));
            Long partidoId = Long.parseLong((String) request.get("partidoId"));
            Double calificacion = Double.parseDouble((String) request.get("calificacion"));
            String comentario = (String) request.get("comentario");
            String fortalezas = (String) request.get("fortalezas");
            String areasMejora = (String) request.get("areasMejora");

            FeedbackPartidoDto feedback = feedbackPartidoService.guardarFeedback(
                    entrenadorId, alumnoId, partidoId,
                    calificacion, comentario, fortalezas, areasMejora);

            return ResponseEntity.ok(feedback);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Valor inválido: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene todos los feedbacks del entrenador.
     */
    @GetMapping("/mis-feedbacks")
    @Operation(summary = "Obtener todos los feedbacks", description = "Recupera todos los feedbacks que ha dado el entrenador")
    public ResponseEntity<List<FeedbackPartidoDto>> obtenerMisFeedbacks(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long entrenadorId = extraerUsuarioId(authHeader);
            List<FeedbackPartidoDto> feedbacks = feedbackPartidoService.obtenerFeedbacksEntrenador(entrenadorId);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
    }

    /**
     * Obtiene los feedbacks para un alumno específico.
     */
    @GetMapping("/alumno/{alumnoId}")
    @Operation(summary = "Obtener feedbacks de un alumno", description = "Recupera todos los feedbacks dados a un alumno específico")
    public ResponseEntity<List<FeedbackPartidoDto>> obtenerFeedbacksAlumno(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long alumnoId) {
        try {
            Long entrenadorId = extraerUsuarioId(authHeader);
            List<FeedbackPartidoDto> feedbacks = feedbackPartidoService.obtenerFeedbacksAlumno(entrenadorId, alumnoId);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
    }

    /**
     * Obtiene todos los feedbacks recibidos por el usuario actual (como alumno).
     */
    @GetMapping("/mis-valoraciones")
    @Operation(summary = "Obtener feedbacks recibidos", description = "Recupera todos los feedbacks que ha recibido el alumno")
    public ResponseEntity<List<FeedbackPartidoDto>> obtenerMisValoraciones(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long alumnoId = extraerUsuarioId(authHeader);
            List<FeedbackPartidoDto> feedbacks = feedbackPartidoService.obtenerFeedbacksRecibidosPorAlumno(alumnoId);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
    }

    /**
     * Obtiene el feedback para un partido específico.
     */
    @GetMapping("/partido/{partidoId}")
    @Operation(summary = "Obtener feedback del partido", description = "Recupera el feedback de un partido específico")
    public ResponseEntity<?> obtenerFeedbackPartido(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long partidoId) {
        try {
            Long entrenadorId = extraerUsuarioId(authHeader);
            FeedbackPartidoDto feedback = feedbackPartidoService.obtenerFeedbackPartido(entrenadorId, partidoId);
            return ResponseEntity.ok(feedback);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verifica si existe feedback para un partido.
     */
    @GetMapping("/existe/{partidoId}")
    @Operation(summary = "Verificar feedback existente", description = "Verifica si existe feedback para un partido")
    public ResponseEntity<Map<String, Boolean>> existeFeedback(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long partidoId) {
        try {
            Long entrenadorId = extraerUsuarioId(authHeader);
            boolean existe = feedbackPartidoService.existeFeedback(entrenadorId, partidoId);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("existe", false));
        }
    }

    /**
     * Elimina el feedback para un partido.
     */
    @DeleteMapping("/{feedbackId}")
    @Operation(summary = "Eliminar feedback", description = "Elimina un feedback específico")
    public ResponseEntity<?> eliminarFeedback(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long feedbackId) {
        try {
            Long entrenadorId = extraerUsuarioId(authHeader);
            feedbackPartidoService.eliminarFeedback(entrenadorId, feedbackId);
            return ResponseEntity.ok(Map.of("mensaje", "Feedback eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Extrae el ID del usuario desde el token JWT.
     */
    private Long extraerUsuarioId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no validado");
        }
        // TODO: Implementar extracción del JWT token
        // Por ahora retorna un ID de ejemplo
        return 1L;
    }
}
