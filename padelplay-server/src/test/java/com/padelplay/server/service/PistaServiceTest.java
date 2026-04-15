package com.padelplay.server.service;

import com.padelplay.server.entity.Pista;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PistaRepository;
import com.padelplay.server.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PistaServiceTest {

    @Mock
    private PistaRepository pistaRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @InjectMocks
    private PistaService pistaService;

    private Pista pista1;
    private Pista pista2;

    @BeforeEach
    void setUp() {
        pista1 = crearPista(1L, "Pista 1", "Centro", "Club Uno");
        pista2 = crearPista(2L, "Pista 2", "Centro", "Club Uno");
    }

    @Test
    void buscarPistasDisponibles_devuelvePistasCuandoNoHaySolapes() {
        when(pistaRepository.findByZonaIgnoreCase("Centro")).thenReturn(List.of(pista1, pista2));
        when(reservaRepository.existsByPistaIdAndInicioLessThanAndFinGreaterThan(any(), any(), any())).thenReturn(false);
        when(partidoRepository.existsByUbicacionIgnoreCaseAndCanceladoFalseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(anyString(), any(), any())).thenReturn(false);

        List<Pista> resultado = pistaService.buscarPistasDisponibles(
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 30),
                "Centro"
        );

        assertEquals(2, resultado.size());
    }

    @Test
    void buscarPistasDisponibles_lanzaExcepcionSiFechaEstaEnElPasado() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                pistaService.buscarPistasDisponibles(
                        LocalDate.now().minusDays(1),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 30),
                        "Centro"
                )
        );

        assertEquals("La fecha y hora de búsqueda no pueden estar en el pasado.", ex.getMessage());
    }

    @Test
    void buscarPistasDisponibles_excluyePistaConReservaEnEseHorario() {
        when(pistaRepository.findByZonaIgnoreCase("Centro")).thenReturn(List.of(pista1, pista2));
        when(reservaRepository.existsByPistaIdAndInicioLessThanAndFinGreaterThan(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);
        when(reservaRepository.existsByPistaIdAndInicioLessThanAndFinGreaterThan(eq(2L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
        when(partidoRepository.existsByUbicacionIgnoreCaseAndCanceladoFalseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(anyString(), any(), any())).thenReturn(false);

        List<Pista> resultado = pistaService.buscarPistasDisponibles(
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 30),
                "Centro"
        );

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getId());
    }

    @Test
    void buscarPistasDisponibles_excluyePistaConPartidoEnEseHorario() {
        when(pistaRepository.findByZonaIgnoreCase("Centro")).thenReturn(List.of(pista1, pista2));
        when(reservaRepository.existsByPistaIdAndInicioLessThanAndFinGreaterThan(any(), any(), any())).thenReturn(false);
        when(partidoRepository.existsByUbicacionIgnoreCaseAndCanceladoFalseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                org.mockito.ArgumentMatchers.eq("Pista 1"), any(), any())).thenReturn(true);
        when(partidoRepository.existsByUbicacionIgnoreCaseAndCanceladoFalseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                org.mockito.ArgumentMatchers.eq("Pista 2"), any(), any())).thenReturn(false);

        List<Pista> resultado = pistaService.buscarPistasDisponibles(
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 30),
                "Centro"
        );

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getId());
    }

    private Pista crearPista(Long id, String nombre, String zona, String club) {
        Pista pista = new Pista();
        pista.setId(id);
        pista.setNombre(nombre);
        pista.setZona(zona);
        pista.setClub(club);
        return pista;
    }
}
