package com.padelplay.server.repository;

import com.padelplay.server.entity.FeedbackPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackPartidoRepository extends JpaRepository<FeedbackPartido, Long> {

    /**
     * Obtiene todos los feedbacks del entrenador.
     */
    List<FeedbackPartido> findByEntrenadorId(Long entrenadorId);

    /**
     * Obtiene todos los feedbacks de un alumno de un entrenador.
     */
    List<FeedbackPartido> findByEntrenadorIdAndAlumnoId(Long entrenadorId, Long alumnoId);

    /**
     * Obtiene el feedback de un partido específico.
     */
    Optional<FeedbackPartido> findByEntrenadorIdAndPartidoId(Long entrenadorId, Long partidoId);

    /**
     * Verifica si existe feedback para un partido.
     */
    boolean existsByEntrenadorIdAndPartidoId(Long entrenadorId, Long partidoId);

    /**
     * Obtiene los feedbacks ordenados por fecha descendente.
     */
    @Query("SELECT f FROM FeedbackPartido f WHERE f.entrenador.id = :entrenadorId ORDER BY f.fechaCreacion DESC")
    List<FeedbackPartido> findByEntrenadorIdOrderByFechaCreacion(@Param("entrenadorId") Long entrenadorId);
}
