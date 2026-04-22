package com.padelplay.server.repository;

import com.padelplay.server.entity.EstadoRecordatorio;
import com.padelplay.server.entity.RecordatorioPartido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordatorioPartidoRepository extends JpaRepository<RecordatorioPartido, Long> {

    Optional<RecordatorioPartido> findByPartidoIdAndDestinatarioEmail(Long partidoId, String destinatarioEmail);

    List<RecordatorioPartido> findByPartidoId(Long partidoId);

    void deleteByPartidoId(Long partidoId);

    @EntityGraph(attributePaths = {"partido"})
    List<RecordatorioPartido> findTop20ByDestinatarioEmailAndProgramadoParaBetweenOrderByProgramadoParaDesc(
            String destinatarioEmail,
            LocalDateTime inicio,
            LocalDateTime fin);

    @EntityGraph(attributePaths = {"partido"})
    List<RecordatorioPartido> findByEstadoAndProgramadoParaLessThanEqual(EstadoRecordatorio estado, LocalDateTime programadoPara);
}