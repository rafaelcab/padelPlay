package com.padelplay.server.config;

import com.padelplay.server.entity.AuthProvider;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.TipoRol;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";

    private final UsuarioRepository usuarioRepository;
    private final PerfilJugadorRepository perfilJugadorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserSeeder(UsuarioRepository usuarioRepository,
                           PerfilJugadorRepository perfilJugadorRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.perfilJugadorRepository = perfilJugadorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Usuario usuario = usuarioRepository.findByEmail(ADMIN_EMAIL).orElseGet(() -> {
            Usuario nuevo = new Usuario();
            nuevo.setNombre("Admin");
            nuevo.setEmail(ADMIN_EMAIL);
            nuevo.setAuthProvider(AuthProvider.LOCAL);
            nuevo.setTienePerfilJugador(true);
            nuevo.setRolActivo(TipoRol.JUGADOR);
            nuevo.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            return usuarioRepository.save(nuevo);
        });

        boolean passwordCorrecta = usuario.getPassword() != null
                && passwordEncoder.matches(ADMIN_PASSWORD, usuario.getPassword());
        if (!passwordCorrecta) {
            usuario.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            usuario.setAuthProvider(AuthProvider.LOCAL);
            usuarioRepository.save(usuario);
        }

        if (perfilJugadorRepository.findByUsuarioId(usuario.getId()).isEmpty()) {
            PerfilJugador perfilJugador = new PerfilJugador();
            perfilJugador.setUsuario(usuario);
            perfilJugador.setApodo("Admin");
            perfilJugador.setNivel(3.0);
            perfilJugadorRepository.save(perfilJugador);
        }

        if (!usuario.isTienePerfilJugador()) {
            usuario.setTienePerfilJugador(true);
            usuario.setRolActivo(TipoRol.JUGADOR);
            usuarioRepository.save(usuario);
        }
    }
}