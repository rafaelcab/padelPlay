package com.padelplay.server.service;

import com.padelplay.common.dto.CertificacionDto;
import com.padelplay.common.dto.DetallesTecnicosDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PerfilEntrenadorDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.server.entity.*;
import com.padelplay.server.repository.CertificacionRepository;
import com.padelplay.server.repository.DetallesTecnicosRepository;
import com.padelplay.server.repository.PerfilEntrenadorRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de perfiles de usuario (Jugador/Entrenador).
 */
@Service
@Transactional
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilJugadorRepository perfilJugadorRepository;
    private final DetallesTecnicosRepository detallesTecnicosRepository;
    private final PerfilEntrenadorRepository perfilEntrenadorRepository;
    private final CertificacionRepository certificacionRepository;

    public PerfilService(UsuarioRepository usuarioRepository,
                         PerfilJugadorRepository perfilJugadorRepository,
                         DetallesTecnicosRepository detallesTecnicosRepository,
                         PerfilEntrenadorRepository perfilEntrenadorRepository,
                         CertificacionRepository certificacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.perfilJugadorRepository = perfilJugadorRepository;
        this.detallesTecnicosRepository = detallesTecnicosRepository;
        this.perfilEntrenadorRepository = perfilEntrenadorRepository;
        this.certificacionRepository = certificacionRepository;
    }

    /**
     * Obtiene el estado actual de los perfiles del usuario.
     */
    @Transactional(readOnly = true)
    public EstadoPerfilDto obtenerEstadoPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        EstadoPerfilDto dto = new EstadoPerfilDto();
        dto.setUsuarioId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setPictureUrl(usuario.getPictureUrl());
        dto.setRolActivo(usuario.getRolActivo() != null ? usuario.getRolActivo().name() : null);
        dto.setTienePerfilJugador(usuario.isTienePerfilJugador());
        dto.setTienePerfilEntrenador(usuario.isTienePerfilEntrenador());
        dto.setRequiereSeleccionPerfil(usuario.requiereSeleccionPerfil());

        if (usuario.isTienePerfilJugador()) {
            perfilJugadorRepository.findByUsuarioIdWithDetalles(usuarioId)
                    .ifPresent(perfil -> dto.setPerfilJugador(convertirADto(perfil)));
        }

        if (usuario.isTienePerfilEntrenador()) {
            perfilEntrenadorRepository.findByUsuarioIdWithCertificaciones(usuarioId)
                    .ifPresent(perfil -> dto.setPerfilEntrenador(convertirADto(perfil)));
        }

        return dto;
    }

    /**
     * Selecciona el rol inicial del usuario.
     */
    public EstadoPerfilDto seleccionarRol(Long usuarioId, TipoRol rol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (rol == TipoRol.JUGADOR) {
            usuario.activarPerfilJugador();
            // Crear perfil de jugador vacío
            if (!perfilJugadorRepository.existsByUsuarioId(usuarioId)) {
                PerfilJugador perfil = new PerfilJugador(usuario);
                DetallesTecnicos detalles = new DetallesTecnicos(perfil);
                perfil.setDetallesTecnicos(detalles);
                perfilJugadorRepository.save(perfil);
            }
        } else if (rol == TipoRol.ENTRENADOR) {
            usuario.activarPerfilEntrenador();
            // Crear perfil de entrenador vacío
            if (!perfilEntrenadorRepository.existsByUsuarioId(usuarioId)) {
                PerfilEntrenador perfil = new PerfilEntrenador(usuario);
                perfilEntrenadorRepository.save(perfil);
            }
        }

        usuarioRepository.save(usuario);
        return obtenerEstadoPerfil(usuarioId);
    }

    /**
     * Cambia el rol activo del usuario (switch entre perfiles).
     */
    public EstadoPerfilDto cambiarRol(Long usuarioId, TipoRol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que el usuario tenga el perfil al que quiere cambiar
        if (nuevoRol == TipoRol.JUGADOR && !usuario.isTienePerfilJugador()) {
            throw new RuntimeException("El usuario no tiene perfil de jugador");
        }
        if (nuevoRol == TipoRol.ENTRENADOR && !usuario.isTienePerfilEntrenador()) {
            throw new RuntimeException("El usuario no tiene perfil de entrenador");
        }

        usuario.setRolActivo(nuevoRol);
        usuarioRepository.save(usuario);
        return obtenerEstadoPerfil(usuarioId);
    }

    /**
     * Crea un nuevo perfil adicional para el usuario.
     */
    public EstadoPerfilDto crearPerfilAdicional(Long usuarioId, TipoRol rol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (rol == TipoRol.JUGADOR && !usuario.isTienePerfilJugador()) {
            return seleccionarRol(usuarioId, TipoRol.JUGADOR);
        } else if (rol == TipoRol.ENTRENADOR && !usuario.isTienePerfilEntrenador()) {
            return seleccionarRol(usuarioId, TipoRol.ENTRENADOR);
        }

        throw new RuntimeException("El usuario ya tiene este tipo de perfil");
    }

    /**
     * Actualiza el perfil de jugador.
     */
    public PerfilJugadorDto actualizarPerfilJugador(Long usuarioId, PerfilJugadorDto dto) {
        PerfilJugador perfil = perfilJugadorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil de jugador no encontrado"));

        perfil.setApodo(dto.getApodo());
        perfil.setAniosExperiencia(dto.getAniosExperiencia());
        perfil.setNivelJuego(dto.getNivelJuego());
        perfil.setTelefono(dto.getTelefono());

        perfilJugadorRepository.save(perfil);
        return convertirADto(perfil);
    }

    /**
     * Actualiza los detalles técnicos del jugador.
     */
    public DetallesTecnicosDto actualizarDetallesTecnicos(Long usuarioId, DetallesTecnicosDto dto) {
        PerfilJugador perfil = perfilJugadorRepository.findByUsuarioIdWithDetalles(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil de jugador no encontrado"));

        DetallesTecnicos detalles = perfil.getDetallesTecnicos();
        if (detalles == null) {
            detalles = new DetallesTecnicos(perfil);
            perfil.setDetallesTecnicos(detalles);
        }

        // Actualizar campos
        if (dto.getPosicion() != null) {
            detalles.setPosicion(Posicion.valueOf(dto.getPosicion()));
        }
        if (dto.getEstiloJuego() != null) {
            detalles.setEstiloJuego(EstiloJuego.valueOf(dto.getEstiloJuego()));
        }
        if (dto.getManoHabil() != null) {
            detalles.setManoHabil(DetallesTecnicos.ManoHabil.valueOf(dto.getManoHabil()));
        }
        if (dto.getGolpesFuertes() != null) {
            Set<TipoGolpe> golpes = dto.getGolpesFuertes().stream()
                    .map(TipoGolpe::valueOf)
                    .collect(Collectors.toSet());
            detalles.setGolpesFuertes(golpes);
        }
        detalles.setObservaciones(dto.getObservaciones());

        detallesTecnicosRepository.save(detalles);
        return convertirADto(detalles);
    }

    /**
     * Obtiene el perfil de jugador del usuario.
     */
    @Transactional(readOnly = true)
    public Optional<PerfilJugadorDto> obtenerPerfilJugador(Long usuarioId) {
        return perfilJugadorRepository.findByUsuarioIdWithDetalles(usuarioId)
                .map(this::convertirADto);
    }

    // === MÉTODOS PARA ENTRENADOR ===

    /**
     * Obtiene el perfil de entrenador del usuario.
     */
    @Transactional(readOnly = true)
    public Optional<PerfilEntrenadorDto> obtenerPerfilEntrenador(Long usuarioId) {
        return perfilEntrenadorRepository.findByUsuarioIdWithCertificaciones(usuarioId)
                .map(this::convertirADto);
    }

    /**
     * Actualiza el perfil de entrenador.
     */
    public PerfilEntrenadorDto actualizarPerfilEntrenador(Long usuarioId, PerfilEntrenadorDto dto) {
        PerfilEntrenador perfil = perfilEntrenadorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil de entrenador no encontrado"));

        perfil.setApodo(dto.getApodo());
        perfil.setAniosExperiencia(dto.getAniosExperiencia());
        perfil.setTelefono(dto.getTelefono());
        perfil.setDescripcion(dto.getDescripcion());
        perfil.setMetodologia(dto.getMetodologia());
        perfil.setUbicacion(dto.getUbicacion());
        perfil.setClubActual(dto.getClubActual());
        perfil.setDisponibleClasesParticulares(dto.getDisponibleClasesParticulares());
        perfil.setDisponibleClasesGrupo(dto.getDisponibleClasesGrupo());
        perfil.setPrecioHoraParticular(dto.getPrecioHoraParticular());
        perfil.setPrecioHoraGrupo(dto.getPrecioHoraGrupo());

        // Actualizar especialidades
        if (dto.getEspecialidades() != null) {
            Set<EspecialidadEntrenador> especialidades = dto.getEspecialidades().stream()
                    .map(EspecialidadEntrenador::valueOf)
                    .collect(Collectors.toSet());
            perfil.setEspecialidades(especialidades);
        }

        perfilEntrenadorRepository.save(perfil);
        return convertirADto(perfil);
    }

    /**
     * Añade una certificación al perfil del entrenador.
     */
    public PerfilEntrenadorDto agregarCertificacion(Long usuarioId, CertificacionDto dto) {
        PerfilEntrenador perfil = perfilEntrenadorRepository.findByUsuarioIdWithCertificaciones(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil de entrenador no encontrado"));

        Certificacion certificacion = new Certificacion();
        certificacion.setTipoCertificacion(TipoCertificacion.valueOf(dto.getTipoCertificacion()));
        certificacion.setNivel(dto.getNivel());
        certificacion.setOrganismo(dto.getOrganismo());
        certificacion.setAnioObtencion(dto.getAnioObtencion());
        certificacion.setNumeroRegistro(dto.getNumeroRegistro());
        certificacion.setVerificada(false);

        perfil.agregarCertificacion(certificacion);
        perfilEntrenadorRepository.save(perfil);

        return convertirADto(perfil);
    }

    /**
     * Elimina una certificación del perfil del entrenador.
     */
    public PerfilEntrenadorDto eliminarCertificacion(Long usuarioId, Long certificacionId) {
        PerfilEntrenador perfil = perfilEntrenadorRepository.findByUsuarioIdWithCertificaciones(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil de entrenador no encontrado"));

        Certificacion certificacion = certificacionRepository.findById(certificacionId)
                .orElseThrow(() -> new RuntimeException("Certificación no encontrada"));

        if (!certificacion.getPerfilEntrenador().getId().equals(perfil.getId())) {
            throw new RuntimeException("La certificación no pertenece a este entrenador");
        }

        perfil.eliminarCertificacion(certificacion);
        certificacionRepository.delete(certificacion);

        return convertirADto(perfil);
    }

    /**
     * Actualiza las certificaciones del entrenador (reemplaza todas).
     */
    public PerfilEntrenadorDto actualizarCertificaciones(Long usuarioId, List<CertificacionDto> certificacionesDto) {
        PerfilEntrenador perfil = perfilEntrenadorRepository.findByUsuarioIdWithCertificaciones(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil de entrenador no encontrado"));

        // Eliminar certificaciones existentes
        perfil.getCertificaciones().clear();

        // Agregar nuevas certificaciones
        for (CertificacionDto dto : certificacionesDto) {
            Certificacion cert = new Certificacion();
            cert.setTipoCertificacion(TipoCertificacion.valueOf(dto.getTipoCertificacion()));
            cert.setNivel(dto.getNivel());
            cert.setOrganismo(dto.getOrganismo());
            cert.setAnioObtencion(dto.getAnioObtencion());
            cert.setNumeroRegistro(dto.getNumeroRegistro());
            cert.setVerificada(false);
            perfil.agregarCertificacion(cert);
        }

        perfilEntrenadorRepository.save(perfil);
        return convertirADto(perfil);
    }

    // === Métodos de conversión ===

    private PerfilJugadorDto convertirADto(PerfilJugador perfil) {
        PerfilJugadorDto dto = new PerfilJugadorDto();
        dto.setId(perfil.getId());
        dto.setApodo(perfil.getApodo());
        dto.setAniosExperiencia(perfil.getAniosExperiencia());
        dto.setNivelJuego(perfil.getNivelJuego());
        dto.setTelefono(perfil.getTelefono());
        
        if (perfil.getDetallesTecnicos() != null) {
            dto.setDetallesTecnicos(convertirADto(perfil.getDetallesTecnicos()));
        }
        
        return dto;
    }

    private DetallesTecnicosDto convertirADto(DetallesTecnicos detalles) {
        DetallesTecnicosDto dto = new DetallesTecnicosDto();
        dto.setId(detalles.getId());
        dto.setPosicion(detalles.getPosicion() != null ? detalles.getPosicion().name() : null);
        dto.setEstiloJuego(detalles.getEstiloJuego() != null ? detalles.getEstiloJuego().name() : null);
        dto.setManoHabil(detalles.getManoHabil() != null ? detalles.getManoHabil().name() : null);
        dto.setObservaciones(detalles.getObservaciones());
        
        if (detalles.getGolpesFuertes() != null) {
            Set<String> golpes = detalles.getGolpesFuertes().stream()
                    .map(TipoGolpe::name)
                    .collect(Collectors.toSet());
            dto.setGolpesFuertes(golpes);
        }
        
        return dto;
    }

    private PerfilEntrenadorDto convertirADto(PerfilEntrenador perfil) {
        PerfilEntrenadorDto dto = new PerfilEntrenadorDto();
        dto.setId(perfil.getId());
        dto.setApodo(perfil.getApodo());
        dto.setAniosExperiencia(perfil.getAniosExperiencia());
        dto.setTelefono(perfil.getTelefono());
        dto.setDescripcion(perfil.getDescripcion());
        dto.setMetodologia(perfil.getMetodologia());
        dto.setUbicacion(perfil.getUbicacion());
        dto.setClubActual(perfil.getClubActual());
        dto.setDisponibleClasesParticulares(perfil.getDisponibleClasesParticulares());
        dto.setDisponibleClasesGrupo(perfil.getDisponibleClasesGrupo());
        dto.setPrecioHoraParticular(perfil.getPrecioHoraParticular());
        dto.setPrecioHoraGrupo(perfil.getPrecioHoraGrupo());

        if (perfil.getEspecialidades() != null) {
            Set<String> especialidades = perfil.getEspecialidades().stream()
                    .map(EspecialidadEntrenador::name)
                    .collect(Collectors.toSet());
            dto.setEspecialidades(especialidades);
        }

        if (perfil.getCertificaciones() != null) {
            List<CertificacionDto> certificaciones = perfil.getCertificaciones().stream()
                    .map(this::convertirADto)
                    .collect(Collectors.toList());
            dto.setCertificaciones(certificaciones);
        }

        return dto;
    }

    private CertificacionDto convertirADto(Certificacion cert) {
        CertificacionDto dto = new CertificacionDto();
        dto.setId(cert.getId());
        dto.setTipoCertificacion(cert.getTipoCertificacion() != null ? cert.getTipoCertificacion().name() : null);
        dto.setNivel(cert.getNivel());
        dto.setOrganismo(cert.getOrganismo());
        dto.setAnioObtencion(cert.getAnioObtencion());
        dto.setNumeroRegistro(cert.getNumeroRegistro());
        dto.setVerificada(cert.getVerificada());
        return dto;
    }
}
