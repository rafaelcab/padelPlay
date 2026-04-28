package com.padelplay.server.repository;

import com.padelplay.server.entity.SolicitudEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudEntrenamientoRepository extends JpaRepository<SolicitudEntrenamiento, Long> {

    boolean existsByJugadorIdAndEntrenadorIdAndEstado(Long jugadorId, Long entrenadorId, String estado);

}
