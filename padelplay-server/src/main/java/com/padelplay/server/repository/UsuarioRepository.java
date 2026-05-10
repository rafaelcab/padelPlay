package com.padelplay.server.repository;

import com.padelplay.server.entity.TipoRol;
import com.padelplay.server.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método original de main - para registro manual
    boolean existsByEmail(String email);
    // Métodos adicionales de Rama_Mikel - para autenticación Google
    Optional<Usuario> findByEmail(String email);
    Usuario findByEmailAndPassword(String email, String password);

    Optional<Usuario> findByGoogleId(String googleId);

    @Query("SELECT u FROM Usuario u WHERE u.rolActivo = :rol OR u.tienePerfilEntrenador = true")
    List<Usuario> findEntrenadoresDisponibles(@Param("rol") TipoRol rol);
}
