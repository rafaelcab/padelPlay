package com.padelplay.server.repository;

import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilJugadorRepository extends JpaRepository<PerfilJugador, Long> {

    Optional<PerfilJugador> findByUsuario(Usuario usuario);

    Optional<PerfilJugador> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);

    @Query("SELECT p FROM PerfilJugador p LEFT JOIN FETCH p.detallesTecnicos WHERE p.usuario.id = :usuarioId")
    Optional<PerfilJugador> findByUsuarioIdWithDetalles(@Param("usuarioId") Long usuarioId);
}
