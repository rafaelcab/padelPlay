package com.padelplay.server.repository;

import com.padelplay.server.entity.PerfilEntrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestión de perfiles de entrenador.
 */
@Repository
public interface PerfilEntrenadorRepository extends JpaRepository<PerfilEntrenador, Long> {

    /**
     * Busca el perfil de entrenador por ID de usuario.
     */
    Optional<PerfilEntrenador> findByUsuarioId(Long usuarioId);

    /**
     * Busca el perfil de entrenador con sus certificaciones cargadas.
     */
    @Query("SELECT pe FROM PerfilEntrenador pe LEFT JOIN FETCH pe.certificaciones WHERE pe.usuario.id = :usuarioId")
    Optional<PerfilEntrenador> findByUsuarioIdWithCertificaciones(@Param("usuarioId") Long usuarioId);

    /**
     * Verifica si existe un perfil de entrenador para el usuario.
     */
    boolean existsByUsuarioId(Long usuarioId);
}
