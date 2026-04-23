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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PistaServicePerformanceTest {

    @Test
    void buscarPistasDisponibles_debeEjecutarseEnMenosDe500msCon500PistasYReservas() {
        PistaRepository pistaRepository = Mockito.mock(PistaRepository.class);
        ReservaRepository reservaRepository = Mockito.mock(ReservaRepository.class);
        PartidoRepository partidoRepository = Mockito.mock(PartidoRepository.class);
        PistaService service = new PistaService(pistaRepository, reservaRepository, partidoRepository);

        List<Pista> pistas = new ArrayList<>();
        for (int i = 1; i <= 500; i++) {
            Pista pista = new Pista();
            pista.setId((long) i);
            pista.setNombre("Pista " + i);
            pista.setZona("Centro");
            pista.setClub("Club Uno");
            pistas.add(pista);
        }

        when(pistaRepository.findByZonaIgnoreCase("Centro")).thenReturn(pistas);
        when(partidoRepository.existsByUbicacionIgnoreCaseAndCanceladoFalseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(anyString(), any(), any())).thenReturn(false);

        for (int i = 1; i <= 500; i++) {
            boolean ocupada = i % 2 == 0;
            when(reservaRepository.existsByPistaIdAndInicioLessThanAndFinGreaterThan(eq((long) i), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(ocupada);
        }

        List<Pista> resultado = assertTimeout(Duration.ofMillis(500), () -> service.buscarPistasDisponibles(
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "Centro"
        ));

        assertEquals(250, resultado.size());
    }
}
