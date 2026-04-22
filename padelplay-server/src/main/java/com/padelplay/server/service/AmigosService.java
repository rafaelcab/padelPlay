package com.padelplay.server.service;

import com.padelplay.common.dto.AmigoPerfilDto;
import com.padelplay.server.entity.DetallesTecnicos;
import com.padelplay.server.entity.PerfilEntrenador;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.SeguimientoAmigo;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.PerfilEntrenadorRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.SeguimientoAmigoRepository;
import com.padelplay.server.repository.UsuarioRepository;
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

    public AmigosService(UsuarioRepository usuarioRepository,
                        PerfilJugadorRepository perfilJugadorRepository,
                        PerfilEntrenadorRepository perfilEntrenadorRepository,
                        SeguimientoAmigoRepository seguimientoAmigoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.perfilJugadorRepository = perfilJugadorRepository;
        this.perfilEntrenadorRepository = perfilEntrenadorRepository;
        this.seguimientoAmigoRepository = seguimientoAmigoRepository;
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
}