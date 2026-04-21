package com.padelplay.server.repository;

import com.padelplay.server.entity.ReporteExperienciaPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReporteExperienciaRepository extends JpaRepository<ReporteExperienciaPartido, Long> {

    boolean existsByPartidoIdAndReportanteIdAndReportadoId(Long partidoId, Long reportanteId, Long reportadoId);

    @Query("""
            select r.reportado.id
            from ReporteExperienciaPartido r
            where r.partido.id = :partidoId
            and r.reportante.id = :reportanteId
            """)
    Set<Long> findReportadoIdsByPartidoIdAndReportanteId(Long partidoId, Long reportanteId);
}
