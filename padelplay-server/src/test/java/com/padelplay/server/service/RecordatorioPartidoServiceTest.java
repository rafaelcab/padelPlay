package com.padelplay.server.service;

import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.RecordatorioPartido;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.RecordatorioPartidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordatorioPartidoServiceTest {

    @Mock
    private RecordatorioPartidoRepository recordatorioPartidoRepository;

    @Mock
    private CorreoRecordatorioService correoRecordatorioService;

    @InjectMocks
    private RecordatorioPartidoService recordatorioPartidoService;

    @Test
    void registrarRecordatoriosIniciales_debeCrearRecordatoriosParaCadaJugador() {
        Usuario u1 = new Usuario();
        u1.setEmail("u1@test.com");
        u1.setNombre("User 1");
        PerfilJugador p1 = new PerfilJugador();
        p1.setUsuario(u1);

        Usuario u2 = new Usuario();
        u2.setEmail("u2@test.com");
        u2.setNombre("User 2");
        PerfilJugador p2 = new PerfilJugador();
        p2.setUsuario(u2);

        Partido partido = new Partido();
        partido.setId(10L);
        partido.setFechaHora(LocalDateTime.now().plusDays(1));
        partido.setJugadoresApuntados(new ArrayList<>(List.of(p1, p2)));

        when(recordatorioPartidoRepository.findByPartidoId(10L)).thenReturn(List.of());

        recordatorioPartidoService.registrarRecordatoriosIniciales(partido);

        verify(recordatorioPartidoRepository, times(2)).save(any(RecordatorioPartido.class));
    }

    @Test
    void eliminarRecordatoriosDePartido_debeLlamarAlRepository() {
        recordatorioPartidoService.eliminarRecordatoriosDePartido(10L);
        verify(recordatorioPartidoRepository).deleteByPartidoId(10L);
    }
}
