package com.padelplay.server.repository;

import com.padelplay.server.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);
    Usuario findByEmailAndPassword(String email, String password);
    Usuario findByEmail(String email);
}