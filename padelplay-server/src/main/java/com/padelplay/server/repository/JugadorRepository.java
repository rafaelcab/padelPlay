package com.padelplay.server.repository;
import com.padelplay.server.entity.Jugador;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    // De momento vacío, Spring Data JPA se encarga del CRUD básico
}