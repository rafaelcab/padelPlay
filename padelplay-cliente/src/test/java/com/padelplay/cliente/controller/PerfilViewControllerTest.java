package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.cliente.proxies.ReporteExperienciaProxy;
import com.padelplay.cliente.proxies.ResultadoPartidoProxy;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.common.dto.ResultadoPartidoPendienteValidacionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PerfilViewController.class)
class PerfilViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PerfilServiceProxy perfilServiceProxy;

    @MockitoBean
    private ReporteExperienciaProxy reporteExperienciaProxy;

    @MockitoBean
    private ResultadoPartidoProxy resultadoPartidoProxy;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void miPerfil_jugadorCargaValidacionesPendientesEnModelo() throws Exception {
        when(perfilServiceProxy.obtenerEstadoPerfil("token-ok")).thenReturn(estadoJugador());
        when(reporteExperienciaProxy.obtenerPartidosJugados("token-ok")).thenReturn(List.of());
        when(resultadoPartidoProxy.obtenerPendientesValidacion("token-ok"))
                .thenReturn(List.of(resultadoPendiente()));

        mockMvc.perform(get("/perfil/mi-perfil").sessionAttr("token", "token-ok"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil-dashboard"))
                .andExpect(model().attributeExists("resultadosPendientesValidacion"))
                .andExpect(model().attribute("resultadosPendientesValidacion", hasSize(1)));
    }

    private EstadoPerfilDto estadoJugador() {
        PerfilJugadorDto perfilJugador = new PerfilJugadorDto();
        perfilJugador.setId(2L);
        perfilJugador.setApodo("jugador2");

        EstadoPerfilDto estado = new EstadoPerfilDto();
        estado.setPerfilJugador(perfilJugador);
        estado.setRolActivo("JUGADOR");
        estado.setTienePerfilJugador(true);
        estado.setRequiereSeleccionPerfil(false);
        return estado;
    }

    private ResultadoPartidoPendienteValidacionDto resultadoPendiente() {
        ResultadoPartidoPendienteValidacionDto dto = new ResultadoPartidoPendienteValidacionDto();
        dto.setPartidoId(9L);
        dto.setUbicacion("Club Norte");
        dto.setFechaHora(LocalDateTime.now().minusHours(2));
        dto.setTipoPartido("ABIERTO");
        dto.setTipoFinalizacion("FINALIZADO_NORMAL");
        dto.setJuegosEquipoA(6);
        dto.setJuegosEquipoB(4);
        dto.setRegistradoPorApodo("creador");
        dto.setEquipoA(List.of(jugador(1L, "creador"), jugador(2L, "jugador2")));
        dto.setEquipoB(List.of(jugador(3L, "jugador3"), jugador(4L, "jugador4")));
        return dto;
    }

    private PerfilJugadorDto jugador(Long id, String apodo) {
        PerfilJugadorDto jugador = new PerfilJugadorDto();
        jugador.setId(id);
        jugador.setApodo(apodo);
        return jugador;
    }
}
