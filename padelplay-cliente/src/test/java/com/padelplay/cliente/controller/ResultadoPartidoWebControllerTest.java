package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.cliente.proxies.ResultadoPartidoProxy;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PartidoDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.common.dto.ResultadoPartidoDto;
import com.padelplay.common.dto.ResultadoPartidoGestionCreadorDto;
import com.padelplay.common.dto.ValidacionResultadoPartidoDto;
import com.padelplay.common.dto.ValidarResultadoPartidoRequestDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ResultadoPartidoWebController.class)
class ResultadoPartidoWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PerfilServiceProxy perfilServiceProxy;

    @MockitoBean
    private ResultadoPartidoProxy resultadoPartidoProxy;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void mostrarFormularioRegistro_creadorValido_renderizaVista() throws Exception {
        when(perfilServiceProxy.obtenerEstadoPerfil("token-ok")).thenReturn(estadoJugador(1L, "creador"));
        when(resultadoPartidoProxy.obtenerResultadosGestionCreador("token-ok"))
                .thenReturn(List.of(gestionResultado(9L, true, false, false, false)));
        when(restTemplate.getForEntity("http://localhost:8080/api/partidos", PartidoDto[].class))
                .thenReturn(new ResponseEntity<>(new PartidoDto[] { partidoDto(9L) }, HttpStatus.OK));

        mockMvc.perform(get("/perfil/resultados/9/registrar").sessionAttr("token", "token-ok"))
                .andExpect(status().isOk())
                .andExpect(view().name("resultado-partido"))
                .andExpect(model().attributeExists("partido"))
                .andExpect(model().attributeExists("resultadoForm"))
                .andExpect(model().attribute("modoResultado", "registro"));
    }

    @Test
    void mostrarFormularioRegistro_sinToken_redirigeALogin() throws Exception {
        mockMvc.perform(get("/perfil/resultados/9/registrar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void registrarResultado_envioValido_redirigeADetalle() throws Exception {
        when(resultadoPartidoProxy.registrarResultado(eq("token-ok"), eq(9L), any()))
                .thenReturn(new ResultadoPartidoDto());

        mockMvc.perform(post("/perfil/resultados/9/registrar")
                        .sessionAttr("token", "token-ok")
                        .param("equipoAJugadorIds[0]", "1")
                        .param("equipoAJugadorIds[1]", "2")
                        .param("equipoBJugadorIds[0]", "3")
                        .param("equipoBJugadorIds[1]", "4")
                        .param("tipoFinalizacion", "FINALIZADO_NORMAL")
                        .param("juegosEquipoA", "6")
                        .param("juegosEquipoB", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil/resultados/9"));
    }

    @Test
    void registrarResultado_errorDelBackend_vuelveAMostrarFormularioConError() throws Exception {
        when(perfilServiceProxy.obtenerEstadoPerfil("token-ok")).thenReturn(estadoJugador(1L, "creador"));
        when(restTemplate.getForEntity("http://localhost:8080/api/partidos", PartidoDto[].class))
                .thenReturn(new ResponseEntity<>(new PartidoDto[] { partidoDto(9L) }, HttpStatus.OK));
        when(resultadoPartidoProxy.registrarResultado(eq("token-ok"), eq(9L), any()))
                .thenThrow(httpBadRequest("Los equipos deben componerse exactamente con los participantes del partido."));
        when(resultadoPartidoProxy.obtenerResultado("token-ok", 9L))
                .thenThrow(httpBadRequest("Sin resultado previo"));

        mockMvc.perform(post("/perfil/resultados/9/registrar")
                        .sessionAttr("token", "token-ok")
                        .param("equipoAJugadorIds[0]", "1")
                        .param("equipoAJugadorIds[1]", "1")
                        .param("equipoBJugadorIds[0]", "3")
                        .param("equipoBJugadorIds[1]", "4")
                        .param("tipoFinalizacion", "FINALIZADO_NORMAL")
                        .param("juegosEquipoA", "6")
                        .param("juegosEquipoB", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("resultado-partido"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void verResultado_jugadorPendientePuedeValidar_renderizaVistaDetalle() throws Exception {
        when(perfilServiceProxy.obtenerEstadoPerfil("token-ok")).thenReturn(estadoJugador(2L, "jugador2"));
        when(resultadoPartidoProxy.obtenerResultado("token-ok", 9L)).thenReturn(resultadoPendiente());

        mockMvc.perform(get("/perfil/resultados/9").sessionAttr("token", "token-ok"))
                .andExpect(status().isOk())
                .andExpect(view().name("resultado-partido"))
                .andExpect(model().attribute("modoResultado", "detalle"))
                .andExpect(model().attribute("puedeValidar", true));
    }

    @Test
    void validarResultado_aceptar_enviaSolicitudAlProxyYRedirige() throws Exception {
        when(resultadoPartidoProxy.validarResultado(eq("token-ok"), eq(9L), any()))
                .thenReturn(resultadoPendiente());

        mockMvc.perform(post("/perfil/resultados/9/validar")
                        .sessionAttr("token", "token-ok")
                        .param("aceptado", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil/resultados/9"));

        ArgumentCaptor<ValidarResultadoPartidoRequestDto> captor =
                ArgumentCaptor.forClass(ValidarResultadoPartidoRequestDto.class);
        verify(resultadoPartidoProxy).validarResultado(eq("token-ok"), eq(9L), captor.capture());
        assertThat(captor.getValue().getAceptado()).isTrue();
    }

    @Test
    void validarResultado_rechazar_enviaSolicitudAlProxyYRedirige() throws Exception {
        when(resultadoPartidoProxy.validarResultado(eq("token-ok"), eq(9L), any()))
                .thenReturn(resultadoPendiente());

        mockMvc.perform(post("/perfil/resultados/9/validar")
                        .sessionAttr("token", "token-ok")
                        .param("aceptado", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil/resultados/9"));

        ArgumentCaptor<ValidarResultadoPartidoRequestDto> captor =
                ArgumentCaptor.forClass(ValidarResultadoPartidoRequestDto.class);
        verify(resultadoPartidoProxy).validarResultado(eq("token-ok"), eq(9L), captor.capture());
        assertThat(captor.getValue().getAceptado()).isFalse();
    }

    private EstadoPerfilDto estadoJugador(Long perfilId, String apodo) {
        PerfilJugadorDto perfilJugador = new PerfilJugadorDto();
        perfilJugador.setId(perfilId);
        perfilJugador.setApodo(apodo);

        EstadoPerfilDto estado = new EstadoPerfilDto();
        estado.setPerfilJugador(perfilJugador);
        estado.setRolActivo("JUGADOR");
        estado.setTienePerfilJugador(true);
        estado.setRequiereSeleccionPerfil(false);
        return estado;
    }

    private PartidoDto partidoDto(Long partidoId) {
        PartidoDto partido = new PartidoDto();
        partido.setId(partidoId);
        partido.setUbicacion("Club Norte");
        partido.setFechaHora(LocalDateTime.now().minusHours(2));
        partido.setJugadoresApuntados(List.of(
                jugador(1L, "creador"),
                jugador(2L, "jugador2"),
                jugador(3L, "jugador3"),
                jugador(4L, "jugador4")
        ));
        return partido;
    }

    private ResultadoPartidoGestionCreadorDto gestionResultado(Long partidoId,
                                                               boolean registrable,
                                                               boolean pendiente,
                                                               boolean rechazado,
                                                               boolean validado) {
        ResultadoPartidoGestionCreadorDto dto = new ResultadoPartidoGestionCreadorDto();
        dto.setPartidoId(partidoId);
        dto.setPuedeRegistrarResultado(registrable);
        dto.setResultadoPendienteValidacion(pendiente);
        dto.setResultadoRechazado(rechazado);
        dto.setResultadoValidado(validado);
        return dto;
    }

    private ResultadoPartidoDto resultadoPendiente() {
        ResultadoPartidoDto resultado = new ResultadoPartidoDto();
        resultado.setPartidoId(9L);
        resultado.setFechaHora(LocalDateTime.now().minusHours(1));
        resultado.setUbicacion("Club Norte");
        resultado.setTipoPartido("ABIERTO");
        resultado.setTipoFinalizacion("FINALIZADO_NORMAL");
        resultado.setJuegosEquipoA(6);
        resultado.setJuegosEquipoB(4);
        resultado.setEstadoValidacion("PENDIENTE_VALIDACION");
        resultado.setRegistradoPorPerfilJugadorId(1L);
        resultado.setRegistradoPorApodo("creador");
        resultado.setEquipoA(List.of(jugador(1L, "creador"), jugador(2L, "jugador2")));
        resultado.setEquipoB(List.of(jugador(3L, "jugador3"), jugador(4L, "jugador4")));

        ValidacionResultadoPartidoDto validacion = new ValidacionResultadoPartidoDto();
        validacion.setPerfilJugadorId(3L);
        validacion.setApodo("jugador3");
        validacion.setAceptado(true);
        validacion.setFechaValidacion(LocalDateTime.now().minusMinutes(15));
        resultado.setValidaciones(List.of(validacion));
        resultado.setValidacionesAprobadas(1);
        resultado.setValidacionesRechazadas(0);
        resultado.setValidacionesPendientes(2);
        return resultado;
    }

    private PerfilJugadorDto jugador(Long id, String apodo) {
        PerfilJugadorDto jugador = new PerfilJugadorDto();
        jugador.setId(id);
        jugador.setApodo(apodo);
        return jugador;
    }

    private HttpClientErrorException httpBadRequest(String message) {
        return HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                message.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
    }
}
