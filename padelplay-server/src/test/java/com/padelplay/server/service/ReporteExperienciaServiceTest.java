package com.padelplay.server.service;

import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.ReporteExperienciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteExperienciaServiceTest {

    @Mock
    private ReporteExperienciaRepository reporteExperienciaRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private PerfilJugadorRepository perfilJugadorRepository;

    @InjectMocks
    private ReporteExperienciaService reporteExperienciaService;

    @Test
    void listarPartidosJugadosConEstado_usuarioNoExiste() {
        when(perfilJugadorRepository.findByUsuarioId(100L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            reporteExperienciaService.listarPartidosJugadosConEstado(100L);
        });
    }

    @Test
    void listarPartidosJugadosConEstado_sinPartidos() {
        PerfilJugador reportante = new PerfilJugador();
        reportante.setId(1L);

        when(perfilJugadorRepository.findByUsuarioId(100L)).thenReturn(Optional.of(reportante));
        when(partidoRepository.findPartidosTerminadosNoCanceladosByJugadorId(1L)).thenReturn(List.of());

        var resultado = reporteExperienciaService.listarPartidosJugadosConEstado(100L);

        assertTrue(resultado.isEmpty());
    }

}
