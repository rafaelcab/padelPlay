package com.padelplay.server.repository;

import com.padelplay.server.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

    @Query("SELECT DISTINCT p FROM Partido p LEFT JOIN p.jugadoresApuntados pj WHERE p.creador.id = :jugadorId OR pj.id = :jugadorId ORDER BY p.fechaHora DESC")
    List<Partido> findPartidosByJugador(@Param("jugadorId") Long jugadorId);
}