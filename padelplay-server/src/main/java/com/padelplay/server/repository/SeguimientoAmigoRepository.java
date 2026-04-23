package com.padelplay.server.repository;

import com.padelplay.server.entity.SeguimientoAmigo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoAmigoRepository extends JpaRepository<SeguimientoAmigo, Long> {

    boolean existsBySeguidorIdAndSeguidoId(Long seguidorId, Long seguidoId);

    long countBySeguidoId(Long seguidoId);

    @Query("select s.seguido.id from SeguimientoAmigo s where s.seguidor.id = :seguidorId")
    List<Long> findSeguidoIdsBySeguidorId(@Param("seguidorId") Long seguidorId);
}