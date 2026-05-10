package com.padelplay.server.service;

import com.padelplay.common.dto.CompletarEvaluacionDto;
import com.padelplay.common.dto.CrearSolicitudEvaluacionDto;
import com.padelplay.common.dto.EntrenadorDisponibleDto;
import com.padelplay.common.dto.ResponderSolicitudEvaluacionDto;
import com.padelplay.common.dto.SolicitudEvaluacionDto;
import com.padelplay.server.entity.EstadoSolicitudEvaluacion;
import com.padelplay.server.entity.HistorialElo;
import com.padelplay.server.entity.SolicitudEvaluacion;
import com.padelplay.server.entity.TipoRol;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.HistorialEloRepository;
import com.padelplay.server.repository.SolicitudEvaluacionRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class EvaluacionService {

    private final SolicitudEvaluacionRepository solicitudEvaluacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialEloRepository historialEloRepository;

    public EvaluacionService(SolicitudEvaluacionRepository solicitudEvaluacionRepository,
                             UsuarioRepository usuarioRepository,
                             HistorialEloRepository historialEloRepository) {
        this.solicitudEvaluacionRepository = solicitudEvaluacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialEloRepository = historialEloRepository;
    }

    @Transactional(readOnly = true)
    public List<EntrenadorDisponibleDto> obtenerEntrenadoresDisponibles() {
        return usuarioRepository.findEntrenadoresDisponibles(TipoRol.ENTRENADOR).stream()
                .map(this::convertirAEntrenadorDisponibleDto)
                .toList();
    }

    public SolicitudEvaluacionDto crearSolicitud(Long jugadorId, CrearSolicitudEvaluacionDto request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la peticion es obligatorio.");
        }
        if (request.getEntrenadorId() == null) {
            throw new IllegalArgumentException("El entrenador es obligatorio.");
        }
        if (request.getFechaHoraSolicitada() == null) {
            throw new IllegalArgumentException("La fecha y hora solicitada es obligatoria.");
        }
        if (jugadorId.equals(request.getEntrenadorId())) {
            throw new IllegalArgumentException("No puedes solicitar una evaluacion a ti mismo.");
        }

        Usuario jugador = obtenerUsuario(jugadorId, "Jugador no encontrado.");
        validarUsuarioJugador(jugador);

        Usuario entrenador = obtenerUsuario(request.getEntrenadorId(), "Entrenador no encontrado.");
        validarUsuarioEntrenador(entrenador);

        SolicitudEvaluacion solicitud = new SolicitudEvaluacion(
                jugadorId,
                request.getEntrenadorId(),
                request.getFechaHoraSolicitada()
        );
        solicitud.setComentarioJugador(request.getComentarioJugador());
        solicitud.setEstado(EstadoSolicitudEvaluacion.PENDIENTE);

        return convertirADto(solicitudEvaluacionRepository.save(solicitud));
    }

    @Transactional(readOnly = true)
    public List<SolicitudEvaluacionDto> obtenerMisSolicitudes(Long jugadorId) {
        return solicitudEvaluacionRepository.findByJugadorIdOrderByFechaCreacionDesc(jugadorId).stream()
                .map(this::convertirADto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitudEvaluacionDto> obtenerSolicitudesRecibidas(Long entrenadorId) {
        Usuario entrenador = obtenerUsuario(entrenadorId, "Entrenador no encontrado.");
        validarUsuarioEntrenador(entrenador);

        return solicitudEvaluacionRepository.findByEntrenadorIdOrderByFechaCreacionDesc(entrenadorId).stream()
                .map(this::convertirADto)
                .toList();
    }

    public SolicitudEvaluacionDto aceptarSolicitud(Long solicitudId,
                                                   Long entrenadorId,
                                                   ResponderSolicitudEvaluacionDto request) {
        return responderSolicitud(solicitudId, entrenadorId, request, EstadoSolicitudEvaluacion.ACEPTADA);
    }

    public SolicitudEvaluacionDto rechazarSolicitud(Long solicitudId,
                                                    Long entrenadorId,
                                                    ResponderSolicitudEvaluacionDto request) {
        return responderSolicitud(solicitudId, entrenadorId, request, EstadoSolicitudEvaluacion.RECHAZADA);
    }

    public SolicitudEvaluacionDto completarSolicitud(Long solicitudId,
                                                     Long entrenadorId,
                                                     CompletarEvaluacionDto request) {
        Usuario entrenador = obtenerUsuario(entrenadorId, "Entrenador no encontrado.");
        validarUsuarioEntrenador(entrenador);

        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la peticion es obligatorio.");
        }
        if (request.getSolicitudId() != null && !request.getSolicitudId().equals(solicitudId)) {
            throw new IllegalArgumentException("El ID de solicitud del cuerpo no coincide con la URL.");
        }
        if (request.getNuevoElo() == null) {
            throw new IllegalArgumentException("El nuevo ELO es obligatorio.");
        }
        if (request.getNuevoElo() < 0 || request.getNuevoElo() > 3000) {
            throw new IllegalArgumentException("El nuevo ELO debe estar entre 0 y 3000.");
        }

        SolicitudEvaluacion solicitud = solicitudEvaluacionRepository.findById(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud de evaluacion no encontrada."));

        if (!solicitud.getEntrenadorId().equals(entrenadorId)) {
            throw new SecurityException("Solo el entrenador destinatario puede completar esta solicitud.");
        }

        if (solicitud.getEstado() != EstadoSolicitudEvaluacion.ACEPTADA) {
            throw new IllegalStateException("Solo se pueden completar solicitudes aceptadas.");
        }

        Usuario jugador = obtenerUsuario(solicitud.getJugadorId(), "Jugador no encontrado.");

        solicitud.setEstado(EstadoSolicitudEvaluacion.COMPLETADA);
        solicitud.setComentarioEntrenador(request.getObservaciones());
        solicitud.setEloAsignado(request.getNuevoElo());
        solicitud.setFechaRespuesta(LocalDateTime.now());

        HistorialElo historialElo = new HistorialElo(jugador, request.getNuevoElo().doubleValue());
        historialElo.setFecha(LocalDateTime.now());
        historialEloRepository.save(historialElo);

        return convertirADto(solicitudEvaluacionRepository.save(solicitud));
    }

    private SolicitudEvaluacionDto responderSolicitud(Long solicitudId,
                                                      Long entrenadorId,
                                                      ResponderSolicitudEvaluacionDto request,
                                                      EstadoSolicitudEvaluacion nuevoEstado) {
        Usuario entrenador = obtenerUsuario(entrenadorId, "Entrenador no encontrado.");
        validarUsuarioEntrenador(entrenador);

        SolicitudEvaluacion solicitud = solicitudEvaluacionRepository.findById(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud de evaluacion no encontrada."));

        if (!solicitud.getEntrenadorId().equals(entrenadorId)) {
            throw new SecurityException("Solo el entrenador destinatario puede responder esta solicitud.");
        }

        if (solicitud.getEstado() != EstadoSolicitudEvaluacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aceptar o rechazar solicitudes pendientes.");
        }

        solicitud.setEstado(nuevoEstado);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        if (request != null) {
            solicitud.setComentarioEntrenador(request.getComentarioEntrenador());
        }

        return convertirADto(solicitudEvaluacionRepository.save(solicitud));
    }

    private Usuario obtenerUsuario(Long usuarioId, String mensajeError) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException(mensajeError));
    }

    private void validarUsuarioJugador(Usuario usuario) {
        boolean esJugador = usuario.isTienePerfilJugador() || usuario.getRolActivo() == TipoRol.JUGADOR;
        if (!esJugador) {
            throw new IllegalStateException("El usuario autenticado no tiene perfil de jugador.");
        }
    }

    private void validarUsuarioEntrenador(Usuario usuario) {
        boolean esEntrenador = usuario.isTienePerfilEntrenador() || usuario.getRolActivo() == TipoRol.ENTRENADOR;
        if (!esEntrenador) {
            throw new IllegalArgumentException("El entrenador indicado no corresponde a un usuario entrenador.");
        }
    }

    private SolicitudEvaluacionDto convertirADto(SolicitudEvaluacion solicitud) {
        SolicitudEvaluacionDto dto = new SolicitudEvaluacionDto();
        dto.setId(solicitud.getId());
        dto.setJugadorId(solicitud.getJugadorId());
        dto.setEntrenadorId(solicitud.getEntrenadorId());
        dto.setFechaHoraSolicitada(solicitud.getFechaHoraSolicitada());
        dto.setEstado(solicitud.getEstado() != null ? solicitud.getEstado().name() : null);
        dto.setComentarioJugador(solicitud.getComentarioJugador());
        dto.setComentarioEntrenador(solicitud.getComentarioEntrenador());
        dto.setFechaCreacion(solicitud.getFechaCreacion());
        dto.setFechaRespuesta(solicitud.getFechaRespuesta());
        dto.setNuevoElo(solicitud.getEloAsignado());

        usuarioRepository.findById(solicitud.getJugadorId())
                .ifPresent(jugador -> dto.setNombreJugador(jugador.getNombre()));
        usuarioRepository.findById(solicitud.getEntrenadorId())
                .ifPresent(entrenador -> dto.setNombreEntrenador(entrenador.getNombre()));

        historialEloRepository.findTopByUsuarioIdOrderByFechaDesc(solicitud.getJugadorId())
                .map(HistorialElo::getElo)
                .map(elo -> Math.toIntExact(Math.round(elo)))
                .ifPresent(dto::setEloActual);

        return dto;
    }

    private EntrenadorDisponibleDto convertirAEntrenadorDisponibleDto(Usuario usuario) {
        EntrenadorDisponibleDto dto = new EntrenadorDisponibleDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setFotoUrl(usuario.getPictureUrl());
        return dto;
    }
}
