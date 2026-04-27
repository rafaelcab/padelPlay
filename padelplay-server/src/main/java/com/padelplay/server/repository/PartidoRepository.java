package com.padelplay.server.repository;

import com.padelplay.server.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

	boolean existsByUbicacionIgnoreCaseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(
			String ubicacion,
			LocalDateTime fechaInicio,
			LocalDateTime fechaFin
	);

	boolean existsByUbicacionIgnoreCaseAndCanceladoFalseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(
			String ubicacion,
			LocalDateTime fechaInicio,
			LocalDateTime fechaFin
	);

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
}
