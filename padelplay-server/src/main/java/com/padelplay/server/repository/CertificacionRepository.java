package com.padelplay.server.repository;

import com.padelplay.server.entity.Certificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestión de certificaciones de entrenadores.
 */
@Repository
public interface CertificacionRepository extends JpaRepository<Certificacion, Long> {

    /**
     * Busca todas las certificaciones de un perfil de entrenador.
     */
    List<Certificacion> findByPerfilEntrenadorId(Long perfilEntrenadorId);

    /**
     * Elimina todas las certificaciones de un perfil de entrenador.
     */
    void deleteByPerfilEntrenadorId(Long perfilEntrenadorId);
}
