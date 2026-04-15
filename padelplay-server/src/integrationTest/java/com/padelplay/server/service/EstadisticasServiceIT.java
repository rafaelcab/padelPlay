package com.padelplay.server.service;

import com.padelplay.common.dto.OcupacionClubDto;
import com.padelplay.server.entity.AuthProvider;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.Pista;
import com.padelplay.server.entity.Reserva;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.PistaRepository;
import com.padelplay.server.repository.ReservaRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class EstadisticasServiceIT {

    @Autowired
    private EstadisticasService estadisticasService;

    @Autowired
    private PistaRepository pistaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilJugadorRepository perfilJugadorRepository;

    @Test
    void calcularOcupacionPorHora_debeReflejarReservaYPartidoEnH2() {
        LocalDate fecha = LocalDate.now().plusDays(1);

        Pista pista1 = new Pista();
        pista1.setNombre("Pista 1");
        pista1.setZona("Centro");
        pista1.setClub("Club Uno");
        pista1 = pistaRepository.save(pista1);

        Pista pista2 = new Pista();
        pista2.setNombre("Pista 2");
        pista2.setZona("Centro");
        pista2.setClub("Club Uno");
        pista2 = pistaRepository.save(pista2);

        Reserva reserva = new Reserva();
        reserva.setPista(pista1);
        reserva.setInicio(LocalDateTime.of(fecha, LocalTime.of(10, 0)));
        reserva.setFin(LocalDateTime.of(fecha, LocalTime.of(11, 30)));
        reservaRepository.save(reserva);

        Usuario usuario = new Usuario();
        usuario.setEmail("stats@padelplay.com");
        usuario.setNombre("Stats");
        usuario.setPassword("123456");
        usuario.setAuthProvider(AuthProvider.LOCAL);
        usuario = usuarioRepository.save(usuario);

        PerfilJugador creador = new PerfilJugador();
        creador.setUsuario(usuario);
        creador.setApodo("stats-player");
        creador.setNivel(3.0);
        creador = perfilJugadorRepository.save(creador);

        Partido partido = new Partido();
        partido.setFechaHora(LocalDateTime.of(fecha, LocalTime.of(18, 0)));
        partido.setUbicacion("Pista 2");
        partido.setTipoPartido("ABIERTO");
        partido.setNivelRequerido(3.0);
        partido.setHuecosDisponibles(3);
        partido.setCreador(creador);
        partido.setJugadoresApuntados(new ArrayList<>(List.of(creador)));
        partidoRepository.save(partido);

        List<OcupacionClubDto> resultado = estadisticasService.calcularOcupacionPorHora(fecha);

        assertEquals(50.0, porcentajeEnHora(resultado, "10:00"));
        assertEquals(50.0, porcentajeEnHora(resultado, "18:00"));
        assertEquals(0.0, porcentajeEnHora(resultado, "12:00"));
    }

    private double porcentajeEnHora(List<OcupacionClubDto> resultado, String hora) {
        return resultado.stream()
                .filter(dto -> hora.equals(dto.getHora()))
                .findFirst()
                .orElseThrow()
                .getPorcentajeOcupacion();
    }
}
