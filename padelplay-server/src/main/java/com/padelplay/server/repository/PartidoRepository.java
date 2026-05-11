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

        @Query("SELECT DISTINCT p FROM Partido p " +
               "LEFT JOIN FETCH p.creador c " +
               "LEFT JOIN FETCH p.jugadoresApuntados pj " +
               "ORDER BY p.fechaHora DESC")
        List<Partido> findPartidosDeAlumnos(@Param("entrenadorId") Long entrenadorId);

	@Query("select distinct p.ubicacion from Partido p where p.cancelado = false and p.fechaHora < :fin and p.fechaHora >= :inicio")
	List<String> findDistinctUbicacionesOcupadasEnFranja(LocalDateTime inicio, LocalDateTime fin);

	@Query("""
			select distinct p
			from Partido p
			join fetch p.creador c
			join p.jugadoresApuntados j
			where j.id = :perfilJugadorId
			and p.cancelado = false
			and p.terminado = true
			order by p.fechaHora desc
			""")
	List<Partido> findPartidosTerminadosNoCanceladosByJugadorId(Long perfilJugadorId);

	@Query("""
			select distinct p
			from Partido p
			left join fetch p.jugadoresApuntados
			where p.creador.id = :creadorId
			order by p.fechaHora desc
			""")
	List<Partido> findByCreadorIdWithJugadores(@Param("creadorId") Long creadorId);
}

