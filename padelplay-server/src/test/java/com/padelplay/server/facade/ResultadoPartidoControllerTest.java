package com.padelplay.server.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.padelplay.common.dto.RegistrarResultadoPartidoRequestDto;
import com.padelplay.common.dto.ResultadoPartidoDto;
import com.padelplay.common.dto.ValidarResultadoPartidoRequestDto;
import com.padelplay.server.service.JwtService;
import com.padelplay.server.service.ResultadoPartidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultadoPartidoController.class)
class ResultadoPartidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ResultadoPartidoService resultadoPartidoService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void registrarResultado_requestValido_devuelveResultadoEnJson() throws Exception {
        RegistrarResultadoPartidoRequestDto request = new RegistrarResultadoPartidoRequestDto();
        request.setEquipoAJugadorIds(List.of(1L, 2L));
        request.setEquipoBJugadorIds(List.of(3L, 4L));
        request.setTipoFinalizacion("FINALIZADO_NORMAL");
        request.setJuegosEquipoA(6);
        request.setJuegosEquipoB(4);

        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(100L);
        when(resultadoPartidoService.registrarResultado(eq(9L), eq(100L), any(RegistrarResultadoPartidoRequestDto.class)))
                .thenReturn(resultadoDto("PENDIENTE_VALIDACION"));

        mockMvc.perform(post("/api/partidos/9/resultado")
                        .header("Authorization", "Bearer token-ok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partidoId").value(9))
                .andExpect(jsonPath("$.estadoValidacion").value("PENDIENTE_VALIDACION"))
                .andExpect(jsonPath("$.juegosEquipoA").value(6));
    }

    @Test
    void registrarResultado_tokenInvalido_devuelve401() throws Exception {
        RegistrarResultadoPartidoRequestDto request = new RegistrarResultadoPartidoRequestDto();
        request.setEquipoAJugadorIds(List.of(1L, 2L));
        request.setEquipoBJugadorIds(List.of(3L, 4L));
        request.setTipoFinalizacion("FINALIZADO_NORMAL");
        request.setJuegosEquipoA(6);
        request.setJuegosEquipoB(4);

        when(jwtService.validarToken("token-bad")).thenReturn(false);

        mockMvc.perform(post("/api/partidos/9/resultado")
                        .header("Authorization", "Bearer token-bad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validarResultado_requestValido_devuelveResultadoActualizado() throws Exception {
        ValidarResultadoPartidoRequestDto request = new ValidarResultadoPartidoRequestDto();
        request.setAceptado(true);

        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(101L);
        when(resultadoPartidoService.validarResultado(eq(9L), eq(101L), any(ValidarResultadoPartidoRequestDto.class)))
                .thenReturn(resultadoDto("VALIDADO"));

        mockMvc.perform(post("/api/partidos/9/resultado/validaciones")
                        .header("Authorization", "Bearer token-ok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoValidacion").value("VALIDADO"))
                .andExpect(jsonPath("$.validacionesAprobadas").value(3));
    }

    @Test
    void validarResultado_errorDeNegocio_devuelve400() throws Exception {
        ValidarResultadoPartidoRequestDto request = new ValidarResultadoPartidoRequestDto();
        request.setAceptado(true);

        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(101L);
        when(resultadoPartidoService.validarResultado(eq(9L), eq(101L), any(ValidarResultadoPartidoRequestDto.class)))
                .thenThrow(new IllegalStateException("Ya has validado el resultado de este partido."));

                mockMvc.perform(post("/api/partidos/9/resultado/validaciones")
                        .header("Authorization", "Bearer token-ok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ya has validado el resultado de este partido."));
    }

    @Test
    void obtenerResultado_requestValido_devuelveResultado() throws Exception {
        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(100L);
        when(resultadoPartidoService.obtenerResultado(9L, 100L)).thenReturn(resultadoDto("PENDIENTE_VALIDACION"));

        mockMvc.perform(get("/api/partidos/9/resultado")
                        .header("Authorization", "Bearer token-ok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partidoId").value(9))
                .andExpect(jsonPath("$.registradoPorApodo").value("creador"));
    }

    private ResultadoPartidoDto resultadoDto(String estado) {
        ResultadoPartidoDto dto = new ResultadoPartidoDto();
        dto.setPartidoId(9L);
        dto.setTipoFinalizacion("FINALIZADO_NORMAL");
        dto.setJuegosEquipoA(6);
        dto.setJuegosEquipoB(4);
        dto.setEstadoValidacion(estado);
        dto.setFechaRegistro(LocalDateTime.now());
        dto.setRegistradoPorPerfilJugadorId(1L);
        dto.setRegistradoPorApodo("creador");
        dto.setPartidoTerminado(true);
        dto.setValidacionesAprobadas("VALIDADO".equals(estado) ? 3 : 0);
        dto.setValidacionesRechazadas(0);
        dto.setValidacionesPendientes("VALIDADO".equals(estado) ? 0 : 3);
        return dto;
    }
}
