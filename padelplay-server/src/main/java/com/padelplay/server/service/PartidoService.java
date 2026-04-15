package com.padelplay.server.service;

import com.padelplay.common.dto.PartidoDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.PartidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final PerfilJugadorRepository perfilJugadorRepository;

    public PartidoService(PartidoRepository partidoRepository, PerfilJugadorRepository perfilJugadorRepository) {
        this.partidoRepository = partidoRepository;
        this.perfilJugadorRepository = perfilJugadorRepository;
    }

    /**
     * Obtiene todos los partidos para el Dashboard y los convierte a DTO.
     */
    @Transactional
    public List<PartidoDto> listarPartidos() {
        List<Partido> partidos = partidoRepository.findAll();

        List<Partido> sinJugadores = partidos.stream()
                .filter(p -> p.getJugadoresApuntados() == null || p.getJugadoresApuntados().isEmpty())
                .toList();

        if (!sinJugadores.isEmpty()) {
            partidoRepository.deleteAll(sinJugadores);
        }

        return partidos.stream()
                .filter(p -> p.getJugadoresApuntados() != null && !p.getJugadoresApuntados().isEmpty())
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo partido a partir de la información del DTO.
     */
    @Transactional
    public PartidoDto crearPartido(PartidoDto dto) {
        if (dto.getCreador() == null || dto.getCreador().getId() == null) {
            throw new IllegalArgumentException("Es necesario especificar un perfil de creador válido.");
        }

        Long idCreador = dto.getCreador().getId();
        PerfilJugador creador = perfilJugadorRepository.findById(idCreador)
                .orElseThrow(() -> new IllegalArgumentException("El perfil con ID " + idCreador + " no existe."));

        validarPartido(dto);

        Partido partido = new Partido();
        partido.setFechaHora(dto.getFechaHora());
        partido.setUbicacion(dto.getUbicacion());
        partido.setNivelRequerido(dto.getNivelRequerido());

        if ("private".equalsIgnoreCase(dto.getTipoPartido())) {
            partido.setTipoPartido("PRIVADO");
            partido.setCodigoAcceso(generarCodigoAleatorio());
        } else {
            partido.setTipoPartido("ABIERTO");
            partido.setCodigoAcceso(null);
        }

        partido.setHuecosDisponibles(3);
        partido.setCreador(creador);

        partido.setJugadoresApuntados(new ArrayList<>());
        partido.getJugadoresApuntados().add(creador);

        Partido partidoGuardado = partidoRepository.save(partido);
        return convertirADto(partidoGuardado);
    }

    // =========================================================================
    // UNIRSE A UN PARTIDO (Actualizado con validación de código)
    // =========================================================================
    @Transactional
    public PartidoDto unirseAPartido(Long partidoId, Long jugadorId, String codigoAcceso) {
        // 1. Buscar el partido
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("El partido no existe."));

        if (partido.isCancelado()) {
            throw new IllegalStateException("Este partido está cancelado y no admite inscripciones.");
        }

        // 2. Buscar al jugador
        PerfilJugador jugador = perfilJugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new IllegalArgumentException("El jugador no existe."));

        // 3. Comprobar aforo
        if (partido.getHuecosDisponibles() <= 0) {
            throw new IllegalStateException("El partido ya está completo.");
        }

        // 4. Comprobar que no sea el creador intentando unirse como invitado
        if (partido.getCreador().getId().equals(jugadorId)) {
            throw new IllegalStateException("Eres el creador, ya estás en este partido.");
        }

        // 5. Comprobar que no esté ya en la lista
        boolean yaApuntado = partido.getJugadoresApuntados().stream()
                .anyMatch(j -> j.getId().equals(jugadorId));

        if (yaApuntado) {
            throw new IllegalStateException("Ya estás apuntado a este partido.");
        }

        // --- NUEVA REGLA: Validar código si es privado ---
        if ("PRIVADO".equalsIgnoreCase(partido.getTipoPartido())) {
            if (codigoAcceso == null || !codigoAcceso.equalsIgnoreCase(partido.getCodigoAcceso())) {
                throw new IllegalStateException("Código de acceso incorrecto para este partido privado.");
            }
        }

        // 6. Acción: Añadir jugador y restar hueco
        partido.getJugadoresApuntados().add(jugador);
        partido.setHuecosDisponibles(partido.getHuecosDisponibles() - 1);

        // 7. Guardar y devolver DTO actualizado
        Partido partidoActualizado = partidoRepository.save(partido);
        return convertirADto(partidoActualizado);
    }

    @Transactional
    public PartidoDto cancelarAsistencia(Long partidoId, Long usuarioId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("El partido no existe."));

        PerfilJugador usuario = perfilJugadorRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        boolean estaApuntado = partido.getJugadoresApuntados().stream()
                .anyMatch(j -> j.getId().equals(usuarioId));

        if (!estaApuntado) {
            throw new IllegalStateException("El usuario no está apuntado a este partido.");
        }

        partido.getJugadoresApuntados().removeIf(j -> j.getId().equals(usuarioId));

        if (partido.getJugadoresApuntados().isEmpty()) {
            PartidoDto dto = convertirADto(partido);
            partidoRepository.delete(partido);
            return dto;
        }

        if (partido.getCreador().getId().equals(usuarioId)) {
            partido.setCancelado(true);
            partido.setHuecosDisponibles(0);
        } else {
            partido.setHuecosDisponibles(Math.min(3, partido.getHuecosDisponibles() + 1));
        }

        Partido partidoActualizado = partidoRepository.save(partido);
        return convertirADto(partidoActualizado);
    }

    @Transactional
    public void eliminarPartidoSiSolo(Long partidoId, Long usuarioId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("El partido no existe."));

        perfilJugadorRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        boolean esCreador = partido.getCreador().getId().equals(usuarioId);
        if (!esCreador) {
            throw new IllegalStateException("Solo el creador puede eliminar el partido.");
        }

        boolean esUnicoJugador = partido.getJugadoresApuntados() != null && partido.getJugadoresApuntados().size() == 1;
        if (!esUnicoJugador) {
            throw new IllegalStateException("Solo puedes eliminar el partido cuando estás solo en él.");
        }

        partidoRepository.delete(partido);
    }

    // === MÉTODOS AUXILIARES ===

    private void validarPartido(PartidoDto dto) {
        if (dto.getNivelRequerido() < 1.0 || dto.getNivelRequerido() > 5.0) {
            throw new IllegalArgumentException("El nivel requerido debe estar entre 1.0 y 5.0");
        }
        if (dto.getFechaHora() == null || dto.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha del partido debe ser en el futuro.");
        }
    }

    private PartidoDto convertirADto(Partido p) {
        PartidoDto dto = new PartidoDto();
        dto.setId(p.getId());
        dto.setFechaHora(p.getFechaHora());
        dto.setUbicacion(p.getUbicacion());
        dto.setTipoPartido(p.getTipoPartido());
        dto.setNivelRequerido(p.getNivelRequerido());
        dto.setHuecosDisponibles(p.getHuecosDisponibles());
        dto.setCodigoAcceso(p.getCodigoAcceso());
        dto.setCancelado(p.isCancelado());

        PerfilJugadorDto creadorDto = new PerfilJugadorDto();
        creadorDto.setId(p.getCreador().getId());
        creadorDto.setApodo(p.getCreador().getApodo());
        creadorDto.setNivel(p.getCreador().getNivel());
        dto.setCreador(creadorDto);

        List<PerfilJugadorDto> jugadoresDtos = p.getJugadoresApuntados().stream()
                .map(j -> {
                    PerfilJugadorDto jDto = new PerfilJugadorDto();
                    jDto.setId(j.getId());
                    jDto.setApodo(j.getApodo());
                    return jDto;
                }).collect(Collectors.toList());

        dto.setJugadoresApuntados(jugadoresDtos);

        return dto;
    }

    private String generarCodigoAleatorio() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        while (codigo.length() < 6) {
            int index = (int) (rnd.nextFloat() * caracteres.length());
            codigo.append(caracteres.charAt(index));
        }
        return codigo.toString();
    }
}