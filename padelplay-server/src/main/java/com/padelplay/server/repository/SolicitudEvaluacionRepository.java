package com.padelplay.server.repository;

import com.padelplay.server.entity.EstadoSolicitudEvaluacion;
import com.padelplay.server.entity.SolicitudEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudEvaluacionRepository extends JpaRepository<SolicitudEvaluacion, Long> {

    List<SolicitudEvaluacion> findByJugadorIdOrderByFechaCreacionDesc(Long jugadorId);

    List<SolicitudEvaluacion> findByEntrenadorIdOrderByFechaCreacionDesc(Long entrenadorId);

    List<SolicitudEvaluacion> findByEntrenadorIdAndEstado(Long entrenadorId, EstadoSolicitudEvaluacion estado);

    List<SolicitudEvaluacion> findByJugadorIdAndEstado(Long jugadorId, EstadoSolicitudEvaluacion estado);
}
