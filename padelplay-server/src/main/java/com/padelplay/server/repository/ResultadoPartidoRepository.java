package com.padelplay.server.repository;

import com.padelplay.server.entity.ResultadoPartido;
import com.padelplay.server.entity.EstadoValidacionResultadoPartido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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
            where p.creador.id = :creadorId
            order by p.fechaHora desc
            """)
    List<ResultadoPartido> findByCreadorIdWithDetalles(@Param("creadorId") Long creadorId);

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
            where r.estadoValidacion = :estado
            and r.registradoPor.id <> :perfilJugadorId
            and (
                r.equipoAJugador1.id = :perfilJugadorId
                or r.equipoAJugador2.id = :perfilJugadorId
                or r.equipoBJugador1.id = :perfilJugadorId
                or r.equipoBJugador2.id = :perfilJugadorId
            )
            and not exists (
                select 1
                from ValidacionResultadoPartido v2
                where v2.resultado = r
                and v2.validador.id = :perfilJugadorId
            )
            order by p.fechaHora desc
            """)
    List<ResultadoPartido> findPendientesValidacionByPerfilJugadorId(@Param("perfilJugadorId") Long perfilJugadorId,
                                                                     @Param("estado") EstadoValidacionResultadoPartido estado);

    @Query("""
            select r
            from ResultadoPartido r
            join fetch r.partido p
            join fetch r.registradoPor
            join fetch r.equipoAJugador1
            join fetch r.equipoAJugador2
            join fetch r.equipoBJugador1
            join fetch r.equipoBJugador2
            where p.cancelado = false
            and p.terminado = true
            and (
                r.equipoAJugador1.id = :perfilJugadorId
                or r.equipoAJugador2.id = :perfilJugadorId
                or r.equipoBJugador1.id = :perfilJugadorId
                or r.equipoBJugador2.id = :perfilJugadorId
            )
            order by p.fechaHora desc, p.id desc
            """)
    List<ResultadoPartido> findTrayectoriaPublicaInicial(@Param("perfilJugadorId") Long perfilJugadorId,
                                                         Pageable pageable);

    @Query("""
            select r
            from ResultadoPartido r
            join fetch r.partido p
            join fetch r.registradoPor
            join fetch r.equipoAJugador1
            join fetch r.equipoAJugador2
            join fetch r.equipoBJugador1
            join fetch r.equipoBJugador2
            where p.cancelado = false
            and p.terminado = true
            and (
                r.equipoAJugador1.id = :perfilJugadorId
                or r.equipoAJugador2.id = :perfilJugadorId
                or r.equipoBJugador1.id = :perfilJugadorId
                or r.equipoBJugador2.id = :perfilJugadorId
            )
            and (
                p.fechaHora < :cursorFechaHora
                or (p.fechaHora = :cursorFechaHora and p.id < :cursorPartidoId)
            )
            order by p.fechaHora desc, p.id desc
            """)
    List<ResultadoPartido> findTrayectoriaPublicaNext(@Param("perfilJugadorId") Long perfilJugadorId,
                                                      @Param("cursorFechaHora") LocalDateTime cursorFechaHora,
                                                      @Param("cursorPartidoId") Long cursorPartidoId,
                                                      Pageable pageable);

    @Query("""
            select r
            from ResultadoPartido r
            join fetch r.partido p
            join fetch r.registradoPor
            join fetch r.equipoAJugador1
            join fetch r.equipoAJugador2
            join fetch r.equipoBJugador1
            join fetch r.equipoBJugador2
            where p.cancelado = false
            and p.terminado = true
            and (
                r.equipoAJugador1.id = :perfilJugadorId
                or r.equipoAJugador2.id = :perfilJugadorId
                or r.equipoBJugador1.id = :perfilJugadorId
                or r.equipoBJugador2.id = :perfilJugadorId
            )
            and (
                p.fechaHora > :cursorFechaHora
                or (p.fechaHora = :cursorFechaHora and p.id > :cursorPartidoId)
            )
            order by p.fechaHora asc, p.id asc
            """)
    List<ResultadoPartido> findTrayectoriaPublicaPrevious(@Param("perfilJugadorId") Long perfilJugadorId,
                                                          @Param("cursorFechaHora") LocalDateTime cursorFechaHora,
                                                          @Param("cursorPartidoId") Long cursorPartidoId,
                                                          Pageable pageable);
}
