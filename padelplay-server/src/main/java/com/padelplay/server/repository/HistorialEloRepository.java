package com.padelplay.server.repository;

import com.padelplay.server.entity.HistorialElo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialEloRepository extends JpaRepository<HistorialElo, Long> {

    List<HistorialElo> findByUsuarioIdOrderByFechaAsc(Long usuarioId);

    Optional<HistorialElo> findTopByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
