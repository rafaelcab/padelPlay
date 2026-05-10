package com.padelplay.server.config;

import com.padelplay.server.entity.AuthProvider;
import com.padelplay.server.entity.HistorialElo;
import com.padelplay.server.entity.TipoRol;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.HistorialEloRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final String USUARIO_PRUEBA_EMAIL = "jugador.demo@padelplay.com";

    private final HistorialEloRepository historialEloRepository;
    private final UsuarioRepository usuarioRepository;

    public DataSeeder(HistorialEloRepository historialEloRepository, UsuarioRepository usuarioRepository) {
        this.historialEloRepository = historialEloRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        Usuario usuarioPrueba = obtenerOCrearUsuarioPrueba();

        if (!historialEloRepository.findByUsuarioIdOrderByFechaRegistroAsc(usuarioPrueba.getId()).isEmpty()) {
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        List<HistorialElo> historial = List.of(
                new HistorialElo(usuarioPrueba.getId(), 980, ahora.minusMonths(5)),
                new HistorialElo(usuarioPrueba.getId(), 1015, ahora.minusMonths(4)),
                new HistorialElo(usuarioPrueba.getId(), 1005, ahora.minusMonths(3)),
                new HistorialElo(usuarioPrueba.getId(), 1050, ahora.minusMonths(2)),
                new HistorialElo(usuarioPrueba.getId(), 1090, ahora.minusMonths(1)),
                new HistorialElo(usuarioPrueba.getId(), 1125, ahora)
        );

        historialEloRepository.saveAll(historial);
    }

    private Usuario obtenerOCrearUsuarioPrueba() {
        return usuarioRepository.findByEmail(USUARIO_PRUEBA_EMAIL)
                .orElseGet(() -> {
                    Usuario usuario = new Usuario();
                    usuario.setEmail(USUARIO_PRUEBA_EMAIL);
                    usuario.setNombre("Jugador Demo");
                    usuario.setPassword("demo");
                    usuario.setAuthProvider(AuthProvider.LOCAL);
                    usuario.setTienePerfilJugador(true);
                    usuario.setRolActivo(TipoRol.JUGADOR);

                    return usuarioRepository.save(usuario);
                });
    }
}
