package com.padelplay.server.facade;

import com.padelplay.common.dto.ResultadoPartidoGestionCreadorDto;
import com.padelplay.common.dto.ResultadoPartidoPendienteValidacionDto;
import com.padelplay.server.service.JwtService;
import com.padelplay.server.service.ResultadoPartidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultadoPartidoDashboardController.class)
class ResultadoPartidoDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResultadoPartidoService resultadoPartidoService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void listarPendientesValidacion_tokenValido_devuelveListaJson() throws Exception {
        ResultadoPartidoPendienteValidacionDto dto = new ResultadoPartidoPendienteValidacionDto();
        dto.setPartidoId(9L);
        dto.setUbicacion("Club Norte");
        dto.setTipoPartido("ABIERTO");
        dto.setTipoFinalizacion("FINALIZADO_NORMAL");
        dto.setJuegosEquipoA(6);
        dto.setJuegosEquipoB(4);
        dto.setRegistradoPorApodo("creador");
        dto.setEstadoValidacion("PENDIENTE_VALIDACION");
        dto.setFechaHora(LocalDateTime.now());

        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(100L);
        when(resultadoPartidoService.listarPendientesValidacion(100L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/resultados-partido/pendientes-validacion")
                        .header("Authorization", "Bearer token-ok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].partidoId").value(9))
                .andExpect(jsonPath("$[0].registradoPorApodo").value("creador"));
    }

    @Test
    void listarPendientesValidacion_tokenInvalido_devuelve401() throws Exception {
        when(jwtService.validarToken("token-bad")).thenReturn(false);

        mockMvc.perform(get("/api/resultados-partido/pendientes-validacion")
                        .header("Authorization", "Bearer token-bad"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarResultadosGestionCreador_tokenValido_devuelveListaJson() throws Exception {
        ResultadoPartidoGestionCreadorDto dto = new ResultadoPartidoGestionCreadorDto();
        dto.setPartidoId(12L);
        dto.setUbicacion("Club Sur");
        dto.setTipoPartido("PRIVADO");
        dto.setFechaHora(LocalDateTime.now());
        dto.setResultadoPendienteValidacion(true);

        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(100L);
        when(resultadoPartidoService.listarResultadosGestionCreador(100L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/resultados-partido/mis-registros")
                        .header("Authorization", "Bearer token-ok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].partidoId").value(12))
                .andExpect(jsonPath("$[0].resultadoPendienteValidacion").value(true));
    }
}
