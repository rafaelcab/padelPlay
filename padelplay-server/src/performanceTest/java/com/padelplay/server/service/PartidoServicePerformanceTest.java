package com.padelplay.server.service;

import com.padelplay.common.dto.PartidoDto;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PartidoServicePerformanceTest {

    @Test
    void cancelarAsistencia_debeEjecutarseEnMenosDe500msCon500PartidosYUsuarios() {
        PartidoRepository partidoRepository = Mockito.mock(PartidoRepository.class);
        PerfilJugadorRepository perfilJugadorRepository = Mockito.mock(PerfilJugadorRepository.class);
        PartidoService service = new PartidoService(partidoRepository, perfilJugadorRepository);

        Map<Long, Partido> partidos = new HashMap<>();
        Map<Long, PerfilJugador> jugadores = new HashMap<>();

        for (int i = 1; i <= 500; i++) {
            PerfilJugador creador = new PerfilJugador();
            creador.setId((long) (i * 10));
            creador.setApodo("creador-" + i);
            creador.setNivel(3.0);

            PerfilJugador jugador = new PerfilJugador();
            jugador.setId((long) (i * 10 + 1));
            jugador.setApodo("jugador-" + i);
            jugador.setNivel(2.5);

            Partido partido = new Partido();
            partido.setId((long) i);
            partido.setFechaHora(LocalDateTime.now().plusDays(1));
            partido.setUbicacion("Pista " + i);
            partido.setTipoPartido("ABIERTO");
            partido.setNivelRequerido(2.5);
            partido.setHuecosDisponibles(2);
            partido.setCreador(creador);
            partido.setJugadoresApuntados(new ArrayList<>(List.of(creador, jugador)));
            partidos.put((long) i, partido);
            jugadores.put((long) (i * 10 + 1), jugador);
        }

        when(partidoRepository.findById(any())).thenAnswer(invocation -> {
            Long partidoId = invocation.getArgument(0);
            return Optional.ofNullable(partidos.get(partidoId));
        });
        when(perfilJugadorRepository.findById(any())).thenAnswer(invocation -> {
            Long jugadorId = invocation.getArgument(0);
            return Optional.ofNullable(jugadores.get(jugadorId));
        });
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<PartidoDto> resultado = assertTimeout(Duration.ofMillis(500), () -> {
            List<PartidoDto> respuestas = new ArrayList<>();
            for (int i = 1; i <= 500; i++) {
                respuestas.add(service.cancelarAsistencia((long) i, (long) (i * 10 + 1)));
            }
            return respuestas;
        });

        assertEquals(500, resultado.size());
    }
}
