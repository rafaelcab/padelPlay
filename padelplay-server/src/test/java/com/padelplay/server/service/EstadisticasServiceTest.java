package com.padelplay.server.service;

import com.padelplay.common.dto.OcupacionClubDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadisticasServiceTest {

    @Mock
    private PistaRepository pistaRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @InjectMocks
    private EstadisticasService estadisticasService;

    private List<Pista> pistas;

    @BeforeEach
    void setUp() {
        pistas = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> {
                    Pista pista = new Pista();
                    pista.setId((long) i);
                    pista.setNombre("Pista " + i);
                    pista.setZona("Centro");
                    pista.setClub("Club Uno");
                    return pista;
                })
                .toList();
    }

    @Test
    void calcularOcupacionPorHora_diaSinReservasNiPartidosDevuelveCeroEnTodasLasHoras() {
        when(pistaRepository.findAll()).thenReturn(List.of());
        when(partidoRepository.findDistinctUbicacionesOcupadasEnFranja(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<OcupacionClubDto> resultado = estadisticasService.calcularOcupacionPorHora(LocalDate.now().plusDays(1));

        assertEquals(15, resultado.size());
        resultado.forEach(dto -> assertEquals(0.0, dto.getPorcentajeOcupacion()));
    }

    @Test
    void calcularOcupacionPorHora_sinPistasPeroConPartidosDebeReflejarOcupacion() {
        when(pistaRepository.findAll()).thenReturn(List.of());
        when(partidoRepository.findDistinctUbicacionesOcupadasEnFranja(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime fin = invocation.getArgument(1);
                    int horaFranja = fin.minusHours(1).getHour();
                    if (horaFranja == 10) {
                        return java.util.Arrays.asList("Padelderio", "Padelderio", null);
                    }
                    if (horaFranja == 11) {
                        return List.of("Club Norte");
                    }
                    return List.of();
                });

        List<OcupacionClubDto> resultado = estadisticasService.calcularOcupacionPorHora(LocalDate.now().plusDays(1));

        assertEquals(50.0, porcentajeEnHora(resultado, "10:00"));
        assertEquals(50.0, porcentajeEnHora(resultado, "11:00"));
        assertEquals(0.0, porcentajeEnHora(resultado, "12:00"));
    }

    @Test
    void calcularOcupacionPorHora_conDiezPistasYCincoOcupadasALasDiezDevuelveCincuentaPorCiento() {
        when(pistaRepository.findAll()).thenReturn(pistas);
        when(reservaRepository.findDistinctPistaIdsOcupadasEnFranja(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime inicio = invocation.getArgument(0);
                    if (inicio.getHour() == 10) {
                        return List.of(1L, 2L, 3L, 4L, 5L);
                    }
                    return List.of();
                });
        when(partidoRepository.findDistinctUbicacionesOcupadasEnFranja(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<OcupacionClubDto> resultado = estadisticasService.calcularOcupacionPorHora(LocalDate.now().plusDays(1));

        assertEquals(50.0, porcentajeEnHora(resultado, "10:00"));
        assertEquals(0.0, porcentajeEnHora(resultado, "11:00"));
    }

    @Test
    void calcularOcupacionPorHora_fechaNulaLanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> estadisticasService.calcularOcupacionPorHora(null));

        assertEquals("La fecha es obligatoria.", ex.getMessage());
    }

    @Test
    void calcularOcupacionPorHora_soportaNombresDuplicadosYNulos() {
        Pista pistaDuplicada1 = new Pista();
        pistaDuplicada1.setId(1L);
        pistaDuplicada1.setNombre("Pista A");
        pistaDuplicada1.setZona("Centro");
        pistaDuplicada1.setClub("Club Uno");

        Pista pistaDuplicada2 = new Pista();
        pistaDuplicada2.setId(2L);
        pistaDuplicada2.setNombre("Pista A");
        pistaDuplicada2.setZona("Centro");
        pistaDuplicada2.setClub("Club Uno");

        when(pistaRepository.findAll()).thenReturn(new ArrayList<>(List.of(pistaDuplicada1, pistaDuplicada2)));
        when(reservaRepository.findDistinctPistaIdsOcupadasEnFranja(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(1L));
        when(partidoRepository.findDistinctUbicacionesOcupadasEnFranja(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(buildUbicacionesConNulo());

        List<OcupacionClubDto> resultado = estadisticasService.calcularOcupacionPorHora(LocalDate.now().plusDays(1));

        assertEquals(50.0, porcentajeEnHora(resultado, "08:00"));
    }

    private double porcentajeEnHora(List<OcupacionClubDto> resultado, String hora) {
        return resultado.stream()
                .filter(dto -> hora.equals(dto.getHora()))
                .findFirst()
                .orElseThrow()
                .getPorcentajeOcupacion();
    }

    private List<String> buildUbicacionesConNulo() {
        List<String> ubicaciones = new ArrayList<>();
        ubicaciones.add(null);
        ubicaciones.add("Pista A");
        return ubicaciones;
    }
}
