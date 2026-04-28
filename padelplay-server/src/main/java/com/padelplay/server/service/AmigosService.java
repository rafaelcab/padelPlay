package com.padelplay.server.service;

import com.padelplay.common.dto.AmigoPerfilDto;
import com.padelplay.common.dto.JugadorEquipoPublicoDto;
import com.padelplay.common.dto.PartidoJugadoPublicoDto;
import com.padelplay.common.dto.PartidosJugadosPublicosCursorDto;
import com.padelplay.server.entity.DetallesTecnicos;
import com.padelplay.server.entity.PerfilEntrenador;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.ResultadoPartido;
import com.padelplay.server.entity.SeguimientoAmigo;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.ResultadoPartidoRepository;
import com.padelplay.server.repository.PerfilEntrenadorRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.SeguimientoAmigoRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class AmigosService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilJugadorRepository perfilJugadorRepository;
    private final PerfilEntrenadorRepository perfilEntrenadorRepository;
    private final SeguimientoAmigoRepository seguimientoAmigoRepository;
    private final ResultadoPartidoRepository resultadoPartidoRepository;
    private final TrayectoriaCursorService trayectoriaCursorService;

    public AmigosService(UsuarioRepository usuarioRepository,
                        PerfilJugadorRepository perfilJugadorRepository,
                        PerfilEntrenadorRepository perfilEntrenadorRepository,
                        SeguimientoAmigoRepository seguimientoAmigoRepository,
                        ResultadoPartidoRepository resultadoPartidoRepository,
                        TrayectoriaCursorService trayectoriaCursorService) {
        this.usuarioRepository = usuarioRepository;
        this.perfilJugadorRepository = perfilJugadorRepository;
        this.perfilEntrenadorRepository = perfilEntrenadorRepository;
        this.seguimientoAmigoRepository = seguimientoAmigoRepository;
        this.resultadoPartidoRepository = resultadoPartidoRepository;
        this.trayectoriaCursorService = trayectoriaCursorService;
    }

    @Transactional(readOnly = true)
    public List<AmigoPerfilDto> listarAmigos(Long usuarioActualId) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<PerfilJugador> perfilesJugador = perfilJugadorRepository.findAllWithDetalles();
        List<PerfilEntrenador> perfilesEntrenador = perfilEntrenadorRepository.findAllWithCertificaciones();

        Map<Long, PerfilJugador> jugadorPorUsuario = new HashMap<>();
        for (PerfilJugador perfilJugador : perfilesJugador) {
            if (perfilJugador.getUsuario() != null) {
                jugadorPorUsuario.put(perfilJugador.getUsuario().getId(), perfilJugador);
            }
        }

        Map<Long, PerfilEntrenador> entrenadorPorUsuario = new HashMap<>();
        for (PerfilEntrenador perfilEntrenador : perfilesEntrenador) {
            if (perfilEntrenador.getUsuario() != null) {
                entrenadorPorUsuario.put(perfilEntrenador.getUsuario().getId(), perfilEntrenador);
            }
        }

        Set<Long> seguidosPorUsuario = new HashSet<>(seguimientoAmigoRepository.findSeguidoIdsBySeguidorId(usuarioActualId));
        List<AmigoPerfilDto> resultado = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(usuarioActualId)) {
                continue;
            }

            AmigoPerfilDto dto = construirDto(
                    usuario,
                    jugadorPorUsuario.get(usuario.getId()),
                    entrenadorPorUsuario.get(usuario.getId()),
                    seguidosPorUsuario.contains(usuario.getId())
            );
            resultado.add(dto);
        }

        resultado.sort(Comparator.comparing(a -> a.getNombre() != null ? a.getNombre().toLowerCase() : "zzz"));
        return resultado;
    }

    @Transactional(readOnly = true)
    public AmigoPerfilDto obtenerAmigo(Long usuarioActualId, Long usuarioObjetivoId) {
        Usuario usuario = usuarioRepository.findById(usuarioObjetivoId)
                .orElseThrow(() -> new IllegalArgumentException("El perfil solicitado no existe."));

        PerfilJugador perfilJugador = perfilJugadorRepository.findByUsuarioIdWithDetalles(usuarioObjetivoId).orElse(null);
        PerfilEntrenador perfilEntrenador = perfilEntrenadorRepository.findByUsuarioIdWithCertificaciones(usuarioObjetivoId).orElse(null);
        boolean seguido = seguimientoAmigoRepository.existsBySeguidorIdAndSeguidoId(usuarioActualId, usuarioObjetivoId);

        return construirDto(usuario, perfilJugador, perfilEntrenador, seguido);
    }

    @Transactional(readOnly = true)
    public PartidosJugadosPublicosCursorDto listarPartidosJugadosPublicos(Long usuarioObjetivoId,
                                                                          Integer limitSolicitado,
                                                                          String cursor,
                                                                          String direction) {
        usuarioRepository.findById(usuarioObjetivoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("El perfil solicitado no existe."));

        int limit = normalizarLimit(limitSolicitado);
        String normalizedDirection = normalizarDirection(direction);

        PerfilJugador perfilJugador = perfilJugadorRepository.findByUsuarioId(usuarioObjetivoId).orElse(null);
        if (perfilJugador == null) {
            return new PartidosJugadosPublicosCursorDto();
        }

        TrayectoriaCursorService.TrayectoriaCursor decodedCursor = trayectoriaCursorService.decodificar(cursor);

        List<ResultadoPartido> resultados = obtenerResultadosTrayectoria(
                perfilJugador.getId(),
                limit + 1,
                decodedCursor,
                normalizedDirection
        );

        boolean hasExtra = resultados.size() > limit;
        List<ResultadoPartido> pagina = hasExtra ? resultados.subList(0, limit) : resultados;

        if ("previous".equals(normalizedDirection)) {
            pagina = pagina.stream()
                    .sorted(Comparator.comparing((ResultadoPartido r) -> r.getPartido().getFechaHora(), Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing(r -> r.getPartido().getId(), Comparator.reverseOrder()))
                    .toList();
        }

        PartidosJugadosPublicosCursorDto dto = new PartidosJugadosPublicosCursorDto();
        dto.setItems(pagina.stream()
                .map(resultado -> convertirAPartidoJugadoPublicoDto(resultado, perfilJugador.getId()))
                .toList());
        dto.setHasNext(calcularHasNext(normalizedDirection, hasExtra, decodedCursor));
        dto.setHasPrevious(calcularHasPrevious(normalizedDirection, hasExtra, decodedCursor));

        if (!pagina.isEmpty()) {
            ResultadoPartido primero = pagina.get(0);
            ResultadoPartido ultimo = pagina.get(pagina.size() - 1);

            if (dto.isHasPrevious()) {
                dto.setPreviousCursor(trayectoriaCursorService.codificar(
                        primero.getPartido().getFechaHora(),
                        primero.getPartido().getId()
                ));
            }
            if (dto.isHasNext()) {
                dto.setNextCursor(trayectoriaCursorService.codificar(
                        ultimo.getPartido().getFechaHora(),
                        ultimo.getPartido().getId()
                ));
            }
        }

        return dto;
    }

    public void seguir(Long usuarioActualId, Long usuarioObjetivoId) {
        if (usuarioActualId.equals(usuarioObjetivoId)) {
            throw new IllegalArgumentException("No puedes seguirte a ti mismo.");
        }

        if (seguimientoAmigoRepository.existsBySeguidorIdAndSeguidoId(usuarioActualId, usuarioObjetivoId)) {
            return;
        }

        Usuario seguidor = usuarioRepository.findById(usuarioActualId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario actual no encontrado."));
        Usuario seguido = usuarioRepository.findById(usuarioObjetivoId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario a seguir no existe."));

        SeguimientoAmigo seguimientoAmigo = new SeguimientoAmigo();
        seguimientoAmigo.setSeguidor(seguidor);
        seguimientoAmigo.setSeguido(seguido);
        seguimientoAmigoRepository.save(seguimientoAmigo);
    }

    private AmigoPerfilDto construirDto(Usuario usuario,
                                        PerfilJugador perfilJugador,
                                        PerfilEntrenador perfilEntrenador,
                                        boolean seguido) {
        AmigoPerfilDto dto = new AmigoPerfilDto();
        dto.setUsuarioId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setPictureUrl(usuario.getPictureUrl());
        dto.setTienePerfilJugador(perfilJugador != null);
        dto.setTienePerfilEntrenador(perfilEntrenador != null);
        dto.setSeguido(seguido);
        dto.setTotalSeguidores(seguimientoAmigoRepository.countBySeguidoId(usuario.getId()));

        if (perfilJugador != null) {
            dto.setJugadorApodo(perfilJugador.getApodo());
            dto.setJugadorAniosExperiencia(perfilJugador.getAniosExperiencia());
            dto.setJugadorNivelJuego(perfilJugador.getNivelJuego());
            dto.setJugadorTelefono(perfilJugador.getTelefono());
            dto.setJugadorNivel(perfilJugador.getNivel());

            DetallesTecnicos detalles = perfilJugador.getDetallesTecnicos();
            if (detalles != null) {
                dto.setJugadorPosicion(detalles.getPosicion() != null ? detalles.getPosicion().name() : null);
                dto.setJugadorEstiloJuego(detalles.getEstiloJuego() != null ? detalles.getEstiloJuego().name() : null);
                dto.setJugadorManoHabil(detalles.getManoHabil() != null ? detalles.getManoHabil().name() : null);
                dto.setJugadorObservaciones(detalles.getObservaciones());
            }
        }

        if (perfilEntrenador != null) {
            dto.setEntrenadorApodo(perfilEntrenador.getApodo());
            dto.setEntrenadorAniosExperiencia(perfilEntrenador.getAniosExperiencia());
            dto.setEntrenadorTelefono(perfilEntrenador.getTelefono());
            dto.setEntrenadorDescripcion(perfilEntrenador.getDescripcion());
            dto.setEntrenadorMetodologia(perfilEntrenador.getMetodologia());
            dto.setEntrenadorUbicacion(perfilEntrenador.getUbicacion());
            dto.setEntrenadorClubActual(perfilEntrenador.getClubActual());
            dto.setEntrenadorDispLunes(perfilEntrenador.getDispLunes());
            dto.setEntrenadorDispMartes(perfilEntrenador.getDispMartes());
            dto.setEntrenadorDispMiercoles(perfilEntrenador.getDispMiercoles());
            dto.setEntrenadorDispJueves(perfilEntrenador.getDispJueves());
            dto.setEntrenadorDispViernes(perfilEntrenador.getDispViernes());
            dto.setEntrenadorDispSabado(perfilEntrenador.getDispSabado());
            dto.setEntrenadorDispDomingo(perfilEntrenador.getDispDomingo());
        }

        return dto;
    }

    private List<ResultadoPartido> obtenerResultadosTrayectoria(Long perfilJugadorId,
                                                                int limit,
                                                                TrayectoriaCursorService.TrayectoriaCursor cursor,
                                                                String direction) {
        PageRequest pageRequest = PageRequest.of(0, limit);

        if (cursor == null) {
            return resultadoPartidoRepository.findTrayectoriaPublicaInicial(perfilJugadorId, pageRequest);
        }

        if ("previous".equals(direction)) {
            return resultadoPartidoRepository.findTrayectoriaPublicaPrevious(
                    perfilJugadorId,
                    cursor.fechaHora(),
                    cursor.partidoId(),
                    pageRequest
            );
        }

        return resultadoPartidoRepository.findTrayectoriaPublicaNext(
                perfilJugadorId,
                cursor.fechaHora(),
                cursor.partidoId(),
                pageRequest
        );
    }

    private int normalizarLimit(Integer limitSolicitado) {
        if (limitSolicitado == null) {
            return 10;
        }
        if (limitSolicitado < 1 || limitSolicitado > 10) {
            throw new IllegalArgumentException("El limite solicitado no es valido.");
        }
        return limitSolicitado;
    }

    private String normalizarDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            return "next";
        }
        if (!"next".equalsIgnoreCase(direction) && !"previous".equalsIgnoreCase(direction)) {
            throw new IllegalArgumentException("La direccion solicitada no es valida.");
        }
        return direction.toLowerCase();
    }

    private boolean calcularHasNext(String direction,
                                    boolean hasExtra,
                                    TrayectoriaCursorService.TrayectoriaCursor cursor) {
        if ("previous".equals(direction)) {
            return cursor != null;
        }
        return hasExtra;
    }

    private boolean calcularHasPrevious(String direction,
                                        boolean hasExtra,
                                        TrayectoriaCursorService.TrayectoriaCursor cursor) {
        if ("previous".equals(direction)) {
            return hasExtra;
        }
        return cursor != null;
    }

    private PartidoJugadoPublicoDto convertirAPartidoJugadoPublicoDto(ResultadoPartido resultado, Long perfilObjetivoId) {
        PartidoJugadoPublicoDto dto = new PartidoJugadoPublicoDto();
        dto.setPartidoId(resultado.getPartido().getId());
        dto.setFechaHora(resultado.getPartido().getFechaHora());
        dto.setUbicacion(resultado.getPartido().getUbicacion());
        dto.setTipoPartido(resultado.getPartido().getTipoPartido());
        dto.setTipoFinalizacion(resultado.getTipoFinalizacion().name());
        dto.setJuegosEquipoA(resultado.getJuegosEquipoA());
        dto.setJuegosEquipoB(resultado.getJuegosEquipoB());

        List<JugadorEquipoPublicoDto> equipoA = List.of(
                convertirAJugadorEquipoDto(resultado.getEquipoAJugador1(), perfilObjetivoId),
                convertirAJugadorEquipoDto(resultado.getEquipoAJugador2(), perfilObjetivoId)
        );
        List<JugadorEquipoPublicoDto> equipoB = List.of(
                convertirAJugadorEquipoDto(resultado.getEquipoBJugador1(), perfilObjetivoId),
                convertirAJugadorEquipoDto(resultado.getEquipoBJugador2(), perfilObjetivoId)
        );
        dto.setEquipoA(equipoA);
        dto.setEquipoB(equipoB);
        dto.setEquipoUsuarioObjetivo(
                equipoA.stream().anyMatch(JugadorEquipoPublicoDto::isUsuarioObjetivo) ? "A" : "B"
        );

        PerfilJugador creador = resultado.getPartido().getCreador();
        if (creador != null) {
            dto.setCreadorId(creador.getId());
            dto.setCreadorApodo(creador.getApodo());
            dto.setUsuarioObjetivoFueCreador(creador.getId().equals(perfilObjetivoId));
        }

        return dto;
    }

    private JugadorEquipoPublicoDto convertirAJugadorEquipoDto(PerfilJugador jugador, Long perfilObjetivoId) {
        JugadorEquipoPublicoDto dto = new JugadorEquipoPublicoDto();
        dto.setPerfilJugadorId(jugador.getId());
        dto.setApodo(jugador.getApodo());
        dto.setUsuarioObjetivo(jugador.getId().equals(perfilObjetivoId));
        return dto;
    }
}
