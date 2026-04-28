package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.cliente.proxies.ResultadoPartidoProxy;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PartidoDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartidoWebController.class)
class PartidoWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private PerfilServiceProxy perfilServiceProxy;

    @MockitoBean
    private ResultadoPartidoProxy resultadoPartidoProxy;

    @Test
    void terminarPartido_sinResultado_redirigeYllamaAlBackend() throws Exception {
        when(perfilServiceProxy.obtenerEstadoPerfil("token-ok")).thenReturn(estadoJugador());
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/partidos/9/terminar"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PartidoDto.class)
        )).thenReturn(new ResponseEntity<>(new PartidoDto(), HttpStatus.OK));

        mockMvc.perform(post("/partidos/9/terminar").sessionAttr("token", "token-ok"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/partidos"));

        verify(restTemplate).exchange(
                eq("http://localhost:8080/api/partidos/9/terminar"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PartidoDto.class)
        );
    }

    private EstadoPerfilDto estadoJugador() {
        PerfilJugadorDto perfilJugador = new PerfilJugadorDto();
        perfilJugador.setId(1L);

        EstadoPerfilDto estado = new EstadoPerfilDto();
        estado.setPerfilJugador(perfilJugador);
        estado.setTienePerfilJugador(true);
        estado.setRolActivo("JUGADOR");
        return estado;
    }
}
