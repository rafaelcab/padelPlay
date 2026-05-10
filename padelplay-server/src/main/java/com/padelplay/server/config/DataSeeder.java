package com.padelplay.server.config;

import com.padelplay.server.entity.AuthProvider;
import com.padelplay.server.entity.DetallesTecnicos;
import com.padelplay.server.entity.EstiloJuego;
import com.padelplay.server.entity.EspecialidadEntrenador;
import com.padelplay.server.entity.HistorialElo;
import com.padelplay.server.entity.PerfilEntrenador;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.Posicion;
import com.padelplay.server.entity.TipoGolpe;
import com.padelplay.server.entity.TipoRol;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.DetallesTecnicosRepository;
import com.padelplay.server.repository.HistorialEloRepository;
import com.padelplay.server.repository.PerfilEntrenadorRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "test1234";

    private final UsuarioRepository usuarioRepository;
    private final PerfilJugadorRepository perfilJugadorRepository;
    private final DetallesTecnicosRepository detallesTecnicosRepository;
    private final HistorialEloRepository historialEloRepository;
    private final PerfilEntrenadorRepository perfilEntrenadorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      PerfilJugadorRepository perfilJugadorRepository,
                      DetallesTecnicosRepository detallesTecnicosRepository,
                      HistorialEloRepository historialEloRepository,
                      PerfilEntrenadorRepository perfilEntrenadorRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.perfilJugadorRepository = perfilJugadorRepository;
        this.detallesTecnicosRepository = detallesTecnicosRepository;
        this.historialEloRepository = historialEloRepository;
        this.perfilEntrenadorRepository = perfilEntrenadorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        crearJugadorDemo(
                "Carlos Martín",
                "jugador1@padelplay.test",
                "Carlitos",
                "Principiante",
                1,
                2.1,
                "600111001",
                Posicion.DERECHA,
                EstiloJuego.DEFENSIVO,
                Set.of(TipoGolpe.GLOBO, TipoGolpe.PARED),
                "Jugador constante, fuerte desde el fondo.",
                List.of(900.0, 930.0, 925.0, 970.0, 1010.0)
        );

        crearJugadorDemo(
                "Laura Sánchez",
                "jugador2@padelplay.test",
                "LauPadel",
                "Intermedio",
                3,
                3.2,
                "600111002",
                Posicion.REVES,
                EstiloJuego.AGRESIVO,
                Set.of(TipoGolpe.SMASH, TipoGolpe.VOLEA, TipoGolpe.BANDEJA),
                "Busca la red y juega con mucha iniciativa.",
                List.of(1050.0, 1065.0, 1090.0, 1080.0, 1120.0)
        );

        crearJugadorDemo(
                "Miguel Torres",
                "jugador3@padelplay.test",
                "Migue",
                "Inicial",
                0,
                1.6,
                "600111003",
                Posicion.DERECHA,
                EstiloJuego.CONTRAATAQUE,
                Set.of(TipoGolpe.RESTO, TipoGolpe.CHIQUITA),
                "En fase de aprendizaje, mejora rapido la colocacion.",
                List.of(800.0, 820.0, 850.0, 870.0, 910.0)
        );

        crearJugadorDemo(
                "Ana López",
                "jugador4@padelplay.test",
                "AnaSmash",
                "Avanzado",
                5,
                4.1,
                "600111004",
                Posicion.REVES,
                EstiloJuego.AGRESIVO,
                Set.of(TipoGolpe.SMASH, TipoGolpe.VIBORA, TipoGolpe.BANDEJA),
                "Jugadora ofensiva con buen remate y lectura tactica.",
                List.of(1200.0, 1180.0, 1210.0, 1240.0, 1280.0)
        );

        crearEntrenadorDemo(
                "Javier Gómez",
                "entrenador1@padelplay.test",
                "Javi Coach",
                8,
                "600222001",
                "Padel Indoor Bilbao",
                "Bilbao",
                Set.of(EspecialidadEntrenador.INICIACION, EspecialidadEntrenador.PERFECCIONAMIENTO)
        );

        crearEntrenadorDemo(
                "Marta Ruiz",
                "entrenador2@padelplay.test",
                "Marta Pro",
                10,
                "600222002",
                "Club Norte Padel",
                "Getxo",
                Set.of(EspecialidadEntrenador.COMPETICION, EspecialidadEntrenador.TACTICA)
        );

        crearEntrenadorDemo(
                "Sergio Navarro",
                "entrenador3@padelplay.test",
                "Sergio Trainer",
                6,
                "600222003",
                "Padel Center",
                "Barakaldo",
                Set.of(EspecialidadEntrenador.ADULTOS, EspecialidadEntrenador.PREPARACION_FISICA)
        );
    }

    private void crearJugadorDemo(String nombre,
                                  String email,
                                  String apodo,
                                  String nivelJuego,
                                  Integer aniosExperiencia,
                                  Double nivel,
                                  String telefono,
                                  Posicion posicion,
                                  EstiloJuego estiloJuego,
                                  Set<TipoGolpe> golpesFuertes,
                                  String observaciones,
                                  List<Double> puntosElo) {
        Usuario usuario = obtenerOCrearUsuario(nombre, email);
        usuario.setTienePerfilJugador(true);
        usuario.setRolActivo(TipoRol.JUGADOR);
        usuarioRepository.save(usuario);

        PerfilJugador perfil = perfilJugadorRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    PerfilJugador nuevoPerfil = new PerfilJugador(usuario);
                    nuevoPerfil.setApodo(apodo);
                    return perfilJugadorRepository.save(nuevoPerfil);
                });

        perfil.setApodo(apodo);
        perfil.setAniosExperiencia(aniosExperiencia);
        perfil.setNivelJuego(nivelJuego);
        perfil.setNivel(nivel);
        perfil.setTelefono(telefono);
        perfilJugadorRepository.save(perfil);

        if (!detallesTecnicosRepository.existsByPerfilJugadorId(perfil.getId())) {
            DetallesTecnicos detalles = new DetallesTecnicos(perfil);
            detalles.setPosicion(posicion);
            detalles.setEstiloJuego(estiloJuego);
            detalles.setGolpesFuertes(new HashSet<>(golpesFuertes));
            detalles.setManoHabil(DetallesTecnicos.ManoHabil.DIESTRO);
            detalles.setObservaciones(observaciones);
            detallesTecnicosRepository.save(detalles);
            perfil.setDetallesTecnicos(detalles);
            perfilJugadorRepository.save(perfil);
        }

        if (historialEloRepository.findByUsuarioIdOrderByFechaAsc(usuario.getId()).isEmpty()) {
            historialEloRepository.saveAll(crearPuntosElo(usuario, puntosElo));
        }
    }

    private void crearEntrenadorDemo(String nombre,
                                     String email,
                                     String apodo,
                                     Integer aniosExperiencia,
                                     String telefono,
                                     String clubActual,
                                     String ubicacion,
                                     Set<EspecialidadEntrenador> especialidades) {
        Usuario usuario = obtenerOCrearUsuario(nombre, email);
        usuario.setTienePerfilEntrenador(true);
        usuario.setRolActivo(TipoRol.ENTRENADOR);
        usuarioRepository.save(usuario);

        PerfilEntrenador perfil = perfilEntrenadorRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    PerfilEntrenador nuevoPerfil = new PerfilEntrenador(usuario);
                    nuevoPerfil.setApodo(apodo);
                    return perfilEntrenadorRepository.save(nuevoPerfil);
                });

        perfil.setApodo(apodo);
        perfil.setAniosExperiencia(aniosExperiencia);
        perfil.setTelefono(telefono);
        perfil.setClubActual(clubActual);
        perfil.setUbicacion(ubicacion);
        perfil.setDescripcion("Entrenador de padel disponible para clases de prueba.");
        perfil.setMetodologia("Trabajo tecnico, tactico y seguimiento personalizado.");
        perfil.setDisponibleClasesParticulares(true);
        perfil.setDisponibleClasesGrupo(true);
        perfil.setPrecioHoraParticular(35.0);
        perfil.setPrecioHoraGrupo(15.0);
        perfil.setEspecialidades(new HashSet<>(especialidades));
        perfil.setDispLunes("10:00,11:00,18:00,19:00");
        perfil.setDispMiercoles("10:00,11:00,18:00,19:00");
        perfil.setDispViernes("17:00,18:00,19:00");
        perfilEntrenadorRepository.save(perfil);
    }

    private Usuario obtenerOCrearUsuario(String nombre, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario nuevo = new Usuario();
            nuevo.setNombre(nombre);
            nuevo.setEmail(email);
            nuevo.setAuthProvider(AuthProvider.LOCAL);
            nuevo.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
            return usuarioRepository.save(nuevo);
        });

        usuario.setNombre(nombre);
        usuario.setAuthProvider(AuthProvider.LOCAL);
        if (usuario.getPassword() == null || !passwordEncoder.matches(DEMO_PASSWORD, usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
        }
        return usuarioRepository.save(usuario);
    }

    private List<HistorialElo> crearPuntosElo(Usuario usuario, List<Double> puntosElo) {
        LocalDateTime primeraFecha = LocalDateTime.now().minusMonths(puntosElo.size() - 1L);

        return puntosElo.stream()
                .map(elo -> {
                    int indice = puntosElo.indexOf(elo);
                    HistorialElo punto = new HistorialElo(usuario, elo);
                    punto.setFecha(primeraFecha.plusMonths(indice));
                    return punto;
                })
                .toList();
    }
}
