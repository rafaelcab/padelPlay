package com.padelplay.server.repository;

import com.padelplay.server.entity.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Long> {

    List<Pista> findByZonaIgnoreCase(String zona);
}
