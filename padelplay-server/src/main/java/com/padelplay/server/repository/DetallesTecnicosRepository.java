package com.padelplay.server.repository;

import com.padelplay.server.entity.DetallesTecnicos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetallesTecnicosRepository extends JpaRepository<DetallesTecnicos, Long> {

    Optional<DetallesTecnicos> findByPerfilJugadorId(Long perfilJugadorId);

    boolean existsByPerfilJugadorId(Long perfilJugadorId);
}
