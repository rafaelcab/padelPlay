package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.AmigosServiceProxy;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.common.dto.AmigoPerfilDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PartidoJugadoPublicoDto;
import com.padelplay.common.dto.PartidosJugadosPublicosCursorDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AmigosWebController.class)
class AmigosWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AmigosServiceProxy amigosServiceProxy;

    @MockitoBean
    private PerfilServiceProxy perfilServiceProxy;

    @Test
    void detalle_cargaPreviewDeTrayectoriaParaPerfilJugador() throws Exception {
        when(perfilServiceProxy.obtenerEstadoPerfil("token-ok")).thenReturn(estado());
        when(amigosServiceProxy.obtenerAmigo("token-ok", 9L)).thenReturn(amigoJugador(9L));
        when(amigosServiceProxy.obtenerPartidosJugadosPublicos("token-ok", 9L, 3, null, "next"))
                .thenReturn(trayectoria("cursor-next"));

        mockMvc.perform(get("/comunidad/9").sessionAttr("token", "token-ok"))
                .andExpect(status().isOk())
                .andExpect(view().name("amigo-detalle"))
                .andExpect(model().attributeExists("trayectoriaPreview"))
                .andExpect(model().attribute("trayectoriaPreview", org.hamcrest.Matchers.hasProperty("items", hasSize(1))));
    }

    @Test
    void trayectoria_cargaBloquePaginadoPorCursor() throws Exception {
        when(perfilServiceProxy.obtenerEstadoPerfil("token-ok")).thenReturn(estado());
        when(amigosServiceProxy.obtenerAmigo("token-ok", 9L)).thenReturn(amigoJugador(9L));
        when(amigosServiceProxy.obtenerPartidosJugadosPublicos("token-ok", 9L, 10, "cursor-prev", "previous"))
                .thenReturn(trayectoria("cursor-next"));

        mockMvc.perform(get("/comunidad/9/trayectoria")
                        .param("cursor", "cursor-prev")
                        .param("direction", "previous")
                        .sessionAttr("token", "token-ok"))
                .andExpect(status().isOk())
                .andExpect(view().name("amigo-trayectoria"))
                .andExpect(model().attributeExists("trayectoria"))
                .andExpect(model().attributeExists("amigo"));
    }

    @Test
    void detalle_redirigeALoginSiNoHayToken() throws Exception {
        mockMvc.perform(get("/comunidad/9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    private AmigoPerfilDto amigoJugador(Long usuarioId) {
        AmigoPerfilDto dto = new AmigoPerfilDto();
        dto.setUsuarioId(usuarioId);
        dto.setNombre("Ana");
        dto.setJugadorApodo("anita");
        dto.setTienePerfilJugador(true);
        return dto;
    }

    private EstadoPerfilDto estado() {
        EstadoPerfilDto estado = new EstadoPerfilDto();
        estado.setEmail("jugador@test.com");
        return estado;
    }

    private PartidosJugadosPublicosCursorDto trayectoria(String nextCursor) {
        PartidoJugadoPublicoDto partido = new PartidoJugadoPublicoDto();
        partido.setPartidoId(100L);
        partido.setFechaHora(LocalDateTime.now().minusDays(1));
        partido.setUbicacion("Club Norte");
        partido.setTipoPartido("ABIERTO");
        partido.setTipoFinalizacion("FINALIZADO_NORMAL");
        partido.setJuegosEquipoA(6);
        partido.setJuegosEquipoB(4);

        PartidosJugadosPublicosCursorDto dto = new PartidosJugadosPublicosCursorDto();
        dto.setItems(List.of(partido));
        dto.setHasNext(true);
        dto.setNextCursor(nextCursor);
        return dto;
    }
}
