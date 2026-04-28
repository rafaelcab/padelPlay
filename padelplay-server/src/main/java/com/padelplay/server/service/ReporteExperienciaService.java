package com.padelplay.server.service;

import com.padelplay.common.dto.PartidoPendienteReporteDto;
import com.padelplay.common.dto.ParticipantePendienteReporteDto;
import com.padelplay.common.dto.ReporteExperienciaDto;
import com.padelplay.common.dto.ReporteExperienciaRequestDto;
import com.padelplay.server.entity.MotivoReporteExperiencia;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.ReporteExperienciaPartido;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.ReporteExperienciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReporteExperienciaService {

    private static final int LONGITUD_MAXIMA_COMENTARIO = 1000;

    private final ReporteExperienciaRepository reporteExperienciaRepository;
    private final PartidoRepository partidoRepository;
    private final PerfilJugadorRepository perfilJugadorRepository;

    public ReporteExperienciaService(ReporteExperienciaRepository reporteExperienciaRepository,
                                     PartidoRepository partidoRepository,
                                     PerfilJugadorRepository perfilJugadorRepository) {
        this.reporteExperienciaRepository = reporteExperienciaRepository;
        this.partidoRepository = partidoRepository;
        this.perfilJugadorRepository = perfilJugadorRepository;
    }

    @Transactional(readOnly = true)
    public List<ParticipantePendienteReporteDto> listarParticipantesPendientes(Long partidoId, Long usuarioId) {
        PerfilJugador reportante = obtenerPerfilJugador(usuarioId);
        Partido partido = obtenerPartidoReportable(partidoId);
        validarParticipacionEnPartido(partido, reportante);

        Set<Long> yaReportados = reporteExperienciaRepository
                .findReportadoIdsByPartidoIdAndReportanteId(partidoId, reportante.getId());

        return obtenerParticipantesReportables(partido, reportante, yaReportados).stream()
                .map(this::convertirParticipantePendienteDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReporteExperienciaDto crearReporte(Long partidoId, Long usuarioId, ReporteExperienciaRequestDto request) {
        PerfilJugador reportante = obtenerPerfilJugador(usuarioId);
        Partido partido = obtenerPartidoReportable(partidoId);
        validarParticipacionEnPartido(partido, reportante);
        validarRequest(request);

        Long reportadoId = request.getReportadoId();
        if (reportante.getId().equals(reportadoId)) {
            throw new IllegalStateException("No puedes reportarte a ti mismo.");
        }

        PerfilJugador reportado = perfilJugadorRepository.findById(reportadoId)
                .orElseThrow(() -> new IllegalArgumentException("El jugador reportado no existe."));

        boolean participaEnPartido = partido.getJugadoresApuntados().stream()
                .anyMatch(jugador -> jugador.getId().equals(reportadoId));

        if (!participaEnPartido) {
            throw new IllegalStateException("Solo puedes reportar a participantes del partido.");
        }

        boolean yaExiste = reporteExperienciaRepository
                .existsByPartidoIdAndReportanteIdAndReportadoId(partidoId, reportante.getId(), reportadoId);

        if (yaExiste) {
            throw new IllegalStateException("Ya has enviado un reporte para este jugador en este partido.");
        }

        ReporteExperienciaPartido reporte = new ReporteExperienciaPartido();
        reporte.setPartido(partido);
        reporte.setReportante(reportante);
        reporte.setReportado(reportado);
        reporte.setValoracion(request.getValoracion());
        reporte.setMotivos(convertirMotivos(request.getMotivos()));
        reporte.setComentario(normalizarComentario(request.getComentario()));

        return convertirADto(reporteExperienciaRepository.save(reporte));
    }

    @Transactional(readOnly = true)
    public List<PartidoPendienteReporteDto> listarPartidosPendientesDeReportar(Long usuarioId) {
        return listarPartidosJugadosConEstado(usuarioId).stream()
                .filter(dto -> !dto.isReporteCompletado())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartidoPendienteReporteDto> listarPartidosJugadosConEstado(Long usuarioId) {
        PerfilJugador reportante = obtenerPerfilJugador(usuarioId);

        return partidoRepository.findPartidosTerminadosNoCanceladosByJugadorId(reportante.getId()).stream()
                .filter(partido -> tieneOtrosParticipantes(partido, reportante))
                .map(partido -> convertirAPartidoPendienteDto(partido, reportante))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> listarMotivosDisponibles() {
        return Arrays.stream(MotivoReporteExperiencia.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    private PerfilJugador obtenerPerfilJugador(Long usuarioId) {
        return perfilJugadorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no tiene perfil de jugador."));
    }

    private Partido obtenerPartidoReportable(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("El partido no existe."));

        if (partido.isCancelado()) {
            throw new IllegalStateException("No se pueden registrar reportes en un partido cancelado.");
        }

        if (!partido.isTerminado()) {
            throw new IllegalStateException("Solo puedes reportar la experiencia cuando el partido esté terminado.");
        }

        if (partido.getJugadoresApuntados() == null || partido.getJugadoresApuntados().isEmpty()) {
            throw new IllegalStateException("El partido no tiene participantes validos para reportar.");
        }

        return partido;
    }

    private void validarParticipacionEnPartido(Partido partido, PerfilJugador reportante) {
        boolean participa = partido.getJugadoresApuntados().stream()
                .anyMatch(jugador -> jugador.getId().equals(reportante.getId()));

        if (!participa) {
            throw new IllegalStateException("Solo los participantes del partido pueden reportar la experiencia.");
        }
    }

    private void validarRequest(ReporteExperienciaRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la peticion es obligatorio.");
        }

        if (request.getReportadoId() == null) {
            throw new IllegalArgumentException("El jugador reportado es obligatorio.");
        }

        if (request.getValoracion() == null || request.getValoracion() < 1 || request.getValoracion() > 5) {
            throw new IllegalArgumentException("La valoracion debe estar entre 1 y 5.");
        }

        String comentario = request.getComentario();
        if (comentario != null && comentario.trim().length() > LONGITUD_MAXIMA_COMENTARIO) {
            throw new IllegalArgumentException("El comentario no puede superar los 1000 caracteres.");
        }

        convertirMotivos(request.getMotivos());
    }

    private Set<PerfilJugador> obtenerParticipantesReportables(Partido partido,
                                                               PerfilJugador reportante,
                                                               Set<Long> yaReportados) {
        Set<Long> reportados = yaReportados == null ? Collections.emptySet() : yaReportados;

        return partido.getJugadoresApuntados().stream()
                .filter(jugador -> !jugador.getId().equals(reportante.getId()))
                .filter(jugador -> !reportados.contains(jugador.getId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private ParticipantePendienteReporteDto convertirParticipantePendienteDto(PerfilJugador perfilJugador) {
        ParticipantePendienteReporteDto dto = new ParticipantePendienteReporteDto();
        dto.setPerfilJugadorId(perfilJugador.getId());
        dto.setApodo(perfilJugador.getApodo());
        return dto;
    }

    private PartidoPendienteReporteDto convertirAPartidoPendienteDto(Partido partido, PerfilJugador reportante) {
        Set<Long> yaReportados = reporteExperienciaRepository
                .findReportadoIdsByPartidoIdAndReportanteId(partido.getId(), reportante.getId());

        Set<PerfilJugador> participantesReportables = obtenerParticipantesReportables(partido, reportante, yaReportados);
        int participantesPendientes = participantesReportables.size();

        PartidoPendienteReporteDto dto = new PartidoPendienteReporteDto();
        dto.setPartidoId(partido.getId());
        dto.setFechaHora(partido.getFechaHora());
        dto.setUbicacion(partido.getUbicacion());
        dto.setTipoPartido(partido.getTipoPartido());
        dto.setParticipantesPendientes(participantesPendientes);
        dto.setReporteCompletado(participantesPendientes == 0);
        return dto;
    }

    private boolean tieneOtrosParticipantes(Partido partido, PerfilJugador reportante) {
        return partido.getJugadoresApuntados() != null
                && partido.getJugadoresApuntados().stream()
                .anyMatch(jugador -> !jugador.getId().equals(reportante.getId()));
    }

    private Set<MotivoReporteExperiencia> convertirMotivos(Set<String> motivos) {
        if (motivos == null || motivos.isEmpty()) {
            return new LinkedHashSet<>();
        }

        return motivos.stream()
                .map(this::convertirMotivo)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private MotivoReporteExperiencia convertirMotivo(String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Los motivos informados no pueden estar vacios.");
        }

        try {
            return MotivoReporteExperiencia.valueOf(motivo.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Motivo de reporte invalido: " + motivo);
        }
    }

    private String normalizarComentario(String comentario) {
        if (comentario == null) {
            return null;
        }

        String normalizado = comentario.trim();
        return normalizado.isEmpty() ? null : normalizado;
    }

    private ReporteExperienciaDto convertirADto(ReporteExperienciaPartido reporte) {
        ReporteExperienciaDto dto = new ReporteExperienciaDto();
        dto.setId(reporte.getId());
        dto.setPartidoId(reporte.getPartido().getId());
        dto.setReportanteId(reporte.getReportante().getId());
        dto.setReportadoId(reporte.getReportado().getId());
        dto.setValoracion(reporte.getValoracion());
        dto.setMotivos(reporte.getMotivos().stream()
                .map(Enum::name)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        dto.setComentario(reporte.getComentario());
        dto.setFechaCreacion(reporte.getFechaCreacion());
        return dto;
    }
}
