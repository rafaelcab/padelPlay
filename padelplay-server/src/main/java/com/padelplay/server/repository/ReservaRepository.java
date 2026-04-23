package com.padelplay.server.repository;

import com.padelplay.server.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsByPistaIdAndInicioLessThanAndFinGreaterThan(Long pistaId, LocalDateTime fin, LocalDateTime inicio);

    @Query("select distinct r.pista.id from Reserva r where r.inicio < :fin and r.fin > :inicio")
    List<Long> findDistinctPistaIdsOcupadasEnFranja(LocalDateTime inicio, LocalDateTime fin);
}
