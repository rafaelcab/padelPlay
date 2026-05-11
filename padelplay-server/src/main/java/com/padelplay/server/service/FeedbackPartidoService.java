package com.padelplay.server.service;

import com.padelplay.common.dto.FeedbackPartidoDto;
import com.padelplay.common.dto.PartidoDto;
import com.padelplay.server.entity.*;
import com.padelplay.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackPartidoService {

    private final FeedbackPartidoRepository feedbackPartidoRepository;
    private final PerfilEntrenadorRepository perfilEntrenadorRepository;
    private final PartidoRepository partidoRepository;
    private final PerfilJugadorRepository perfilJugadorRepository;
    private final PartidoService partidoService;

    @Autowired
    public FeedbackPartidoService(
            FeedbackPartidoRepository feedbackPartidoRepository,
            PerfilEntrenadorRepository perfilEntrenadorRepository,
            PartidoRepository partidoRepository,
            PerfilJugadorRepository perfilJugadorRepository,
            PartidoService partidoService) {
        this.feedbackPartidoRepository = feedbackPartidoRepository;
        this.perfilEntrenadorRepository = perfilEntrenadorRepository;
        this.partidoRepository = partidoRepository;
        this.perfilJugadorRepository = perfilJugadorRepository;
        this.partidoService = partidoService;
    }

    /**
     * Obtiene el historial de partidos de los alumnos del entrenador.
     * Por ahora retorna todos los partidos del sistema (en futuro filtrará por alumnos).
     */
    @Transactional(readOnly = true)
    public List<PartidoDto> obtenerHistorialPartidosAlumnos(Long entrenadorId) {
        PerfilEntrenador entrenador = perfilEntrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

        // En el futuro, filtrar solo los partidos de los alumnos del entrenador
        // Por ahora: todos los partidos pasados que ya finalizaron
        return partidoService.listarPartidos();
    }

    /**
     * Guarda el feedback de un entrenador para un partido específico.
     */
    public FeedbackPartidoDto guardarFeedback(Long entrenadorId, Long alumnoId, Long partidoId,
                                                Double calificacion, String comentario,
                                                String fortalezas, String areasMejora) {
        PerfilEntrenador entrenador = perfilEntrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

        PerfilJugador alumno = perfilJugadorRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        // Validar que el alumno estuvo en el partido
        if (!partido.getJugadoresApuntados().contains(alumno) && !partido.getCreador().equals(alumno)) {
            throw new RuntimeException("El alumno no participó en este partido");
        }

        // Validar que la calificación esté en el rango correcto
        if (calificacion < 1.0 || calificacion > 5.0) {
            throw new IllegalArgumentException("La calificación debe estar entre 1.0 y 5.0");
        }

        // Actualizar o crear feedback
        FeedbackPartido feedback = feedbackPartidoRepository
                .findByEntrenadorIdAndPartidoId(entrenadorId, partidoId)
                .orElseGet(() -> new FeedbackPartido(entrenador, alumno, partido));

        feedback.setCalificacion(calificacion);
        feedback.setComentario(comentario);
        feedback.setFortalezas(fortalezas);
        feedback.setAreasMejora(areasMejora);

        FeedbackPartido guardado = feedbackPartidoRepository.save(feedback);
        return convertirADto(guardado);
    }

    /**
     * Obtiene todos los feedbacks de un entrenador.
     */
    @Transactional(readOnly = true)
    public List<FeedbackPartidoDto> obtenerFeedbacksEntrenador(Long entrenadorId) {
        return feedbackPartidoRepository.findByEntrenadorIdOrderByFechaCreacion(entrenadorId)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los feedbacks para un alumno específico del entrenador.
     */
    @Transactional(readOnly = true)
    public List<FeedbackPartidoDto> obtenerFeedbacksAlumno(Long entrenadorId, Long alumnoId) {
        return feedbackPartidoRepository.findByEntrenadorIdAndAlumnoId(entrenadorId, alumnoId)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los feedbacks recibidos por un alumno.
     */
    @Transactional(readOnly = true)
    public List<FeedbackPartidoDto> obtenerFeedbacksRecibidosPorAlumno(Long alumnoId) {
        return feedbackPartidoRepository.findByAlumnoIdOrderByFechaCreacionDesc(alumnoId)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el feedback para un partido específico.
     */
    @Transactional(readOnly = true)
    public FeedbackPartidoDto obtenerFeedbackPartido(Long entrenadorId, Long partidoId) {
        return feedbackPartidoRepository.findByEntrenadorIdAndPartidoId(entrenadorId, partidoId)
                .map(this::convertirADto)
                .orElseThrow(() -> new RuntimeException("Feedback no encontrado"));
    }

    /**
     * Verifica si existe feedback para un partido.
     */
    @Transactional(readOnly = true)
    public boolean existeFeedback(Long entrenadorId, Long partidoId) {
        return feedbackPartidoRepository.existsByEntrenadorIdAndPartidoId(entrenadorId, partidoId);
    }

    /**
     * Elimina el feedback para un partido.
     */
    public void eliminarFeedback(Long entrenadorId, Long feedbackId) {
        FeedbackPartido feedback = feedbackPartidoRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback no encontrado"));

        if (!feedback.getEntrenador().getId().equals(entrenadorId)) {
            throw new RuntimeException("No tienes permisos para eliminar este feedback");
        }

        feedbackPartidoRepository.deleteById(feedbackId);
    }

    /**
     * Convierte una entidad FeedbackPartido a DTO.
     */
    private FeedbackPartidoDto convertirADto(FeedbackPartido feedback) {
        FeedbackPartidoDto dto = new FeedbackPartidoDto();
        dto.setId(feedback.getId());
        dto.setEntrenadorId(feedback.getEntrenador().getId());
        dto.setEntrenadorApodo(feedback.getEntrenador().getApodo());
        dto.setAlumnoId(feedback.getAlumno().getId());
        dto.setAlumnoApodo(feedback.getAlumno().getApodo());
        dto.setPartidoId(feedback.getPartido().getId());
        dto.setFechaPartido(feedback.getPartido().getFechaHora());
        dto.setUbicacionPartido(feedback.getPartido().getUbicacion());
        dto.setCalificacion(feedback.getCalificacion());
        dto.setComentario(feedback.getComentario());
        dto.setFortalezas(feedback.getFortalezas());
        dto.setAreasMejora(feedback.getAreasMejora());
        dto.setFechaCreacion(feedback.getFechaCreacion());
        dto.setFechaActualizacion(feedback.getFechaActualizacion());
        return dto;
    }
}
