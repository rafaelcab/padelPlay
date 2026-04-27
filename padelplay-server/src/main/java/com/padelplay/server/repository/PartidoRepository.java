package com.padelplay.server.repository;

import com.padelplay.server.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

        @Query("SELECT DISTINCT p FROM Partido p LEFT JOIN p.jugadoresApuntados pj WHERE p.creador.id = :jugadorId OR pj.id = :jugadorId ORDER BY p.fechaHora DESC")
        List<Partido> findPartidosByJugador(@Param("jugadorId") Long jugadorId);

        boolean existsByUbicacionIgnoreCaseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                        String ubicacion,
                        LocalDateTime fechaInicio,
                        LocalDateTime fechaFin);

        boolean existsByUbicacionIgnoreCaseAndCanceladoFalseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                        String ubicacion,
                        LocalDateTime fechaInicio,
                        LocalDateTime fechaFin);

        @Query("select distinct p.ubicacion from Partido p where p.cancelado = false and p.fechaHora < :fin and p.fechaHora >= :inicio")
        List<String> findDistinctUbicacionesOcupadasEnFranja(LocalDateTime inicio, LocalDateTime fin);

        @Query("SELECT DISTINCT p FROM Partido p WHERE p.creador.usuario IN " +
                        "(SELECT DISTINCT s.jugador FROM SolicitudEntrenamiento s WHERE s.entrenador.usuario.id = :entrenadorId) "
                        +
                        "ORDER BY p.fechaHora DESC")
        List<Partido> findPartidosDeAlumnos(@Param("entrenadorId") Long entrenadorId);
}