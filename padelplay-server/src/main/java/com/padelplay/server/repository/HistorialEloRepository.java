package com.padelplay.server.repository;

import com.padelplay.server.entity.HistorialElo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEloRepository extends JpaRepository<HistorialElo, Long> {

    List<HistorialElo> findByUsuarioIdOrderByFechaRegistroAsc(Long usuarioId);
}
