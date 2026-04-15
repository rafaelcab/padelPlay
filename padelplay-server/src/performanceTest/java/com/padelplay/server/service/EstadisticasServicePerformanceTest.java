package com.padelplay.server.service;

import com.padelplay.server.entity.Pista;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PistaRepository;
import com.padelplay.server.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EstadisticasServicePerformanceTest {

    @Test
    void calcularOcupacionPorHora_debeEjecutarseEnMenosDe500msCon50PistasY500Registros() {
        PistaRepository pistaRepository = Mockito.mock(PistaRepository.class);
        ReservaRepository reservaRepository = Mockito.mock(ReservaRepository.class);
        PartidoRepository partidoRepository = Mockito.mock(PartidoRepository.class);
        EstadisticasService service = new EstadisticasService(pistaRepository, reservaRepository, partidoRepository);

        List<Pista> pistas = new ArrayList<>();
        Map<Integer, List<Long>> reservasPorHora = new HashMap<>();
        Map<Integer, List<String>> partidosPorHora = new HashMap<>();

        for (int i = 1; i <= 50; i++) {
            Pista pista = new Pista();
            pista.setId((long) i);
            pista.setNombre("Pista " + i);
            pista.setZona("Centro");
            pista.setClub("Club Uno");
            pistas.add(pista);
        }

        for (int i = 0; i < 500; i++) {
            int hora = 8 + (i % 15);
            reservasPorHora.computeIfAbsent(hora, key -> new ArrayList<>()).add((long) ((i % 50) + 1));
            partidosPorHora.computeIfAbsent(hora, key -> new ArrayList<>()).add("Pista " + (((i + 10) % 50) + 1));
        }

        when(pistaRepository.findAll()).thenReturn(pistas);
        when(reservaRepository.findDistinctPistaIdsOcupadasEnFranja(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime inicio = invocation.getArgument(0);
                    return reservasPorHora.getOrDefault(inicio.getHour(), List.of());
                });
        when(partidoRepository.findDistinctUbicacionesOcupadasEnFranja(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime inicio = invocation.getArgument(0);
                    return partidosPorHora.getOrDefault(inicio.plusMinutes(90).getHour(), List.of());
                });

        List<?> resultado = assertTimeout(Duration.ofMillis(500), () -> service.calcularOcupacionPorHora(LocalDate.now().plusDays(1)));

        assertEquals(15, resultado.size());
    }
}
