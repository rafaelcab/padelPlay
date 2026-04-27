package com.padelplay.server.service;

import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.common.dto.RegistrarResultadoPartidoRequestDto;
import com.padelplay.common.dto.ResultadoPartidoDto;
import com.padelplay.common.dto.ValidacionResultadoPartidoDto;
import com.padelplay.common.dto.ValidarResultadoPartidoRequestDto;
import com.padelplay.server.entity.EstadoValidacionResultadoPartido;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.ResultadoPartido;
import com.padelplay.server.entity.TipoFinalizacionResultadoPartido;
import com.padelplay.server.entity.ValidacionResultadoPartido;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.ResultadoPartidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResultadoPartidoService {

    private static final int JUGADORES_REQUERIDOS = 4;
    private static final int JUGADORES_POR_EQUIPO = 2;

    private final ResultadoPartidoRepository resultadoPartidoRepository;
    private final PartidoRepository partidoRepository;
    private final PerfilJugadorRepository perfilJugadorRepository;

    public ResultadoPartidoService(ResultadoPartidoRepository resultadoPartidoRepository,
                                   PartidoRepository partidoRepository,
                                   PerfilJugadorRepository perfilJugadorRepository) {
        this.resultadoPartidoRepository = resultadoPartidoRepository;
        this.partidoRepository = partidoRepository;
        this.perfilJugadorRepository = perfilJugadorRepository;
    }

    @Transactional
    public ResultadoPartidoDto registrarResultado(Long partidoId,
                                                  Long usuarioId,
                                                  RegistrarResultadoPartidoRequestDto request) {
        Partido partido = obtenerPartido(partidoId);
        PerfilJugador solicitante = obtenerPerfilJugadorDelUsuario(usuarioId);

        validarRegistroResultado(partido, solicitante, request);

        Map<Long, PerfilJugador> participantesPorId = partido.getJugadoresApuntados().stream()
                .collect(Collectors.toMap(PerfilJugador::getId, jugador -> jugador));

        ResultadoPartido resultado = resultadoPartidoRepository.findByPartidoIdWithDetalles(partidoId).orElse(null);
        if (resultado != null) {
            if (resultado.getEstadoValidacion() == EstadoValidacionResultadoPartido.VALIDADO) {
                throw new IllegalStateException("El resultado del partido ya esta validado y no puede modificarse.");
            }
            if (resultado.getEstadoValidacion() != EstadoValidacionResultadoPartido.RECHAZADO) {
                throw new IllegalStateException("Ya existe un resultado pendiente de validacion para este partido.");
            }
            resultado.getValidaciones().clear();
        } else {
            resultado = new ResultadoPartido();
            resultado.setPartido(partido);
        }

        List<Long> equipoAIds = request.getEquipoAJugadorIds();
        List<Long> equipoBIds = request.getEquipoBJugadorIds();

        resultado.setRegistradoPor(solicitante);
        resultado.setEquipoAJugador1(participantesPorId.get(equipoAIds.get(0)));
        resultado.setEquipoAJugador2(participantesPorId.get(equipoAIds.get(1)));
        resultado.setEquipoBJugador1(participantesPorId.get(equipoBIds.get(0)));
        resultado.setEquipoBJugador2(participantesPorId.get(equipoBIds.get(1)));
        resultado.setTipoFinalizacion(convertirTipoFinalizacion(request.getTipoFinalizacion()));
        resultado.setJuegosEquipoA(request.getJuegosEquipoA());
        resultado.setJuegosEquipoB(request.getJuegosEquipoB());
        resultado.setEstadoValidacion(EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION);

        if (!partido.isTerminado()) {
            partido.setTerminado(true);
            partidoRepository.save(partido);
        }

        return convertirADto(resultadoPartidoRepository.save(resultado));
    }

    @Transactional
    public ResultadoPartidoDto validarResultado(Long partidoId,
                                                Long usuarioId,
                                                ValidarResultadoPartidoRequestDto request) {
        if (request == null || request.getAceptado() == null) {
            throw new IllegalArgumentException("Debes indicar si aceptas o rechazas el resultado.");
        }

        Partido partido = obtenerPartido(partidoId);
        ResultadoPartido resultado = obtenerResultadoExistente(partidoId);
        PerfilJugador validador = obtenerPerfilJugadorDelUsuario(usuarioId);

        if (resultado.getEstadoValidacion() != EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION) {
            throw new IllegalStateException("El resultado actual no admite nuevas validaciones.");
        }

        if (!esParticipante(partido, validador.getId())) {
            throw new IllegalStateException("Solo los participantes del partido pueden validar el resultado.");
        }

        if (resultado.getRegistradoPor().getId().equals(validador.getId())) {
            throw new IllegalStateException("El jugador que registra el resultado no puede validarlo.");
        }

        boolean yaValido = resultado.getValidaciones().stream()
                .anyMatch(validacion -> validacion.getValidador().getId().equals(validador.getId()));
        if (yaValido) {
            throw new IllegalStateException("Ya has validado el resultado de este partido.");
        }

        ValidacionResultadoPartido validacion = new ValidacionResultadoPartido();
        validacion.setResultado(resultado);
        validacion.setValidador(validador);
        validacion.setAceptado(request.getAceptado());
        resultado.getValidaciones().add(validacion);

        recalcularEstadoValidacion(resultado, partido);

        return convertirADto(resultadoPartidoRepository.save(resultado));
    }

    @Transactional(readOnly = true)
    public ResultadoPartidoDto obtenerResultado(Long partidoId, Long usuarioId) {
        Partido partido = obtenerPartido(partidoId);
        PerfilJugador solicitante = obtenerPerfilJugadorDelUsuario(usuarioId);

        if (!esParticipante(partido, solicitante.getId())) {
            throw new IllegalStateException("Solo los participantes del partido pueden consultar el resultado.");
        }

        return convertirADto(obtenerResultadoExistente(partidoId));
    }

    private Partido obtenerPartido(Long partidoId) {
        return partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("El partido no existe."));
    }

    private ResultadoPartido obtenerResultadoExistente(Long partidoId) {
        return resultadoPartidoRepository.findByPartidoIdWithDetalles(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("El partido no tiene un resultado registrado."));
    }

    private PerfilJugador obtenerPerfilJugadorDelUsuario(Long usuarioId) {
        return perfilJugadorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no tiene perfil de jugador."));
    }

    private void validarRegistroResultado(Partido partido,
                                          PerfilJugador solicitante,
                                          RegistrarResultadoPartidoRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la peticion es obligatorio.");
        }

        if (partido.isCancelado()) {
            throw new IllegalStateException("No puedes registrar el resultado de un partido cancelado.");
        }

        if (!partido.getCreador().getId().equals(solicitante.getId())) {
            throw new IllegalStateException("Solo el creador puede registrar el resultado del partido.");
        }

        if (partido.getFechaHora() == null || !partido.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Solo puedes registrar el resultado despues de la hora de inicio.");
        }

        if (partido.getJugadoresApuntados() == null || partido.getJugadoresApuntados().size() != JUGADORES_REQUERIDOS) {
            throw new IllegalStateException("El resultado solo puede registrarse cuando el partido tiene 4 jugadores.");
        }

        validarEquipos(partido, request.getEquipoAJugadorIds(), request.getEquipoBJugadorIds());
        validarMarcador(request);
    }

    private void validarEquipos(Partido partido, List<Long> equipoAIds, List<Long> equipoBIds) {
        if (equipoAIds == null || equipoBIds == null) {
            throw new IllegalArgumentException("Debes indicar los jugadores de ambos equipos.");
        }

        if (equipoAIds.size() != JUGADORES_POR_EQUIPO || equipoBIds.size() != JUGADORES_POR_EQUIPO) {
            throw new IllegalArgumentException("Cada equipo debe estar formado por exactamente 2 jugadores.");
        }

        Set<Long> idsPartido = partido.getJugadoresApuntados().stream()
                .map(PerfilJugador::getId)
                .collect(Collectors.toCollection(HashSet::new));

        List<Long> idsCombinados = new ArrayList<>();
        idsCombinados.addAll(equipoAIds);
        idsCombinados.addAll(equipoBIds);

        Set<Long> idsUnicos = new HashSet<>(idsCombinados);
        if (idsUnicos.size() != JUGADORES_REQUERIDOS) {
            throw new IllegalArgumentException("Cada jugador debe aparecer una sola vez en el resultado.");
        }

        if (!idsPartido.equals(idsUnicos)) {
            throw new IllegalArgumentException("Los equipos deben componerse exactamente con los participantes del partido.");
        }
    }

    private void validarMarcador(RegistrarResultadoPartidoRequestDto request) {
        TipoFinalizacionResultadoPartido tipoFinalizacion = convertirTipoFinalizacion(request.getTipoFinalizacion());
        Integer juegosEquipoA = request.getJuegosEquipoA();
        Integer juegosEquipoB = request.getJuegosEquipoB();

        if (tipoFinalizacion == TipoFinalizacionResultadoPartido.FINALIZADO_NORMAL) {
            if (juegosEquipoA == null || juegosEquipoB == null) {
                throw new IllegalArgumentException("Debes indicar el marcador final de ambos equipos.");
            }
            if (juegosEquipoA < 0 || juegosEquipoB < 0) {
                throw new IllegalArgumentException("El marcador no puede contener valores negativos.");
            }
            if (juegosEquipoA.equals(juegosEquipoB)) {
                throw new IllegalArgumentException("El partido no puede terminar en empate.");
            }
            return;
        }

        if (juegosEquipoA != null && juegosEquipoA < 0 || juegosEquipoB != null && juegosEquipoB < 0) {
            throw new IllegalArgumentException("El marcador no puede contener valores negativos.");
        }
    }

    private TipoFinalizacionResultadoPartido convertirTipoFinalizacion(String tipoFinalizacion) {
        if (tipoFinalizacion == null || tipoFinalizacion.isBlank()) {
            throw new IllegalArgumentException("El tipo de finalizacion es obligatorio.");
        }

        try {
            return TipoFinalizacionResultadoPartido.valueOf(tipoFinalizacion.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de finalizacion invalido: " + tipoFinalizacion);
        }
    }

    private void recalcularEstadoValidacion(ResultadoPartido resultado, Partido partido) {
        long rechazos = resultado.getValidaciones().stream()
                .filter(validacion -> !validacion.isAceptado())
                .count();
        if (rechazos > 0) {
            resultado.setEstadoValidacion(EstadoValidacionResultadoPartido.RECHAZADO);
            return;
        }

        int validadoresEsperados = partido.getJugadoresApuntados().size() - 1;
        if (resultado.getValidaciones().size() >= validadoresEsperados) {
            resultado.setEstadoValidacion(EstadoValidacionResultadoPartido.VALIDADO);
        } else {
            resultado.setEstadoValidacion(EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION);
        }
    }

    private boolean esParticipante(Partido partido, Long perfilJugadorId) {
        return partido.getJugadoresApuntados() != null
                && partido.getJugadoresApuntados().stream()
                .anyMatch(jugador -> jugador.getId().equals(perfilJugadorId));
    }

    private ResultadoPartidoDto convertirADto(ResultadoPartido resultado) {
        ResultadoPartidoDto dto = new ResultadoPartidoDto();
        dto.setPartidoId(resultado.getPartido().getId());
        dto.setTipoFinalizacion(resultado.getTipoFinalizacion().name());
        dto.setJuegosEquipoA(resultado.getJuegosEquipoA());
        dto.setJuegosEquipoB(resultado.getJuegosEquipoB());
        dto.setEstadoValidacion(resultado.getEstadoValidacion().name());
        dto.setFechaRegistro(resultado.getFechaRegistro());
        dto.setPartidoTerminado(resultado.getPartido().isTerminado());
        dto.setRegistradoPorPerfilJugadorId(resultado.getRegistradoPor().getId());
        dto.setRegistradoPorApodo(resultado.getRegistradoPor().getApodo());
        dto.setEquipoA(List.of(
                convertirAPerfilJugadorDto(resultado.getEquipoAJugador1()),
                convertirAPerfilJugadorDto(resultado.getEquipoAJugador2())
        ));
        dto.setEquipoB(List.of(
                convertirAPerfilJugadorDto(resultado.getEquipoBJugador1()),
                convertirAPerfilJugadorDto(resultado.getEquipoBJugador2())
        ));

        List<ValidacionResultadoPartidoDto> validaciones = resultado.getValidaciones().stream()
                .sorted(Comparator.comparing(
                        ValidacionResultadoPartido::getFechaValidacion,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(this::convertirAValidacionDto)
                .toList();
        dto.setValidaciones(validaciones);

        int aprobadas = (int) resultado.getValidaciones().stream()
                .filter(ValidacionResultadoPartido::isAceptado)
                .count();
        int rechazadas = resultado.getValidaciones().size() - aprobadas;
        int pendientes = Math.max(0, JUGADORES_REQUERIDOS - 1 - resultado.getValidaciones().size());

        dto.setValidacionesAprobadas(aprobadas);
        dto.setValidacionesRechazadas(rechazadas);
        dto.setValidacionesPendientes(pendientes);
        return dto;
    }

    private PerfilJugadorDto convertirAPerfilJugadorDto(PerfilJugador perfilJugador) {
        PerfilJugadorDto dto = new PerfilJugadorDto();
        dto.setId(perfilJugador.getId());
        dto.setApodo(perfilJugador.getApodo());
        dto.setNivel(perfilJugador.getNivel());
        return dto;
    }

    private ValidacionResultadoPartidoDto convertirAValidacionDto(ValidacionResultadoPartido validacion) {
        ValidacionResultadoPartidoDto dto = new ValidacionResultadoPartidoDto();
        dto.setPerfilJugadorId(validacion.getValidador().getId());
        dto.setApodo(validacion.getValidador().getApodo());
        dto.setAceptado(validacion.isAceptado());
        dto.setFechaValidacion(validacion.getFechaValidacion());
        return dto;
    }
}
