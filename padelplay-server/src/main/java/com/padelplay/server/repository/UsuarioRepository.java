package com.padelplay.server.repository;

import com.padelplay.server.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método original de main - para registro manual
    boolean existsByEmail(String email);
    
    // Métodos adicionales de Rama_Mikel - para autenticación Google
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByGoogleId(String googleId);
}
