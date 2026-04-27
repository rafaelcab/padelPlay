package com.padelplay.server.repository;

import com.padelplay.server.entity.ResultadoPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultadoPartidoRepository extends JpaRepository<ResultadoPartido, Long> {

    @Query("""
            select distinct r
            from ResultadoPartido r
            join fetch r.partido p
            join fetch r.registradoPor
            join fetch r.equipoAJugador1
            join fetch r.equipoAJugador2
            join fetch r.equipoBJugador1
            join fetch r.equipoBJugador2
            left join fetch r.validaciones v
            left join fetch v.validador
            where p.id = :partidoId
            """)
    Optional<ResultadoPartido> findByPartidoIdWithDetalles(@Param("partidoId") Long partidoId);
}
