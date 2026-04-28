package com.padelplay.server.facade;

import com.padelplay.common.dto.PartidoDto;
import com.padelplay.server.service.JwtService;
import com.padelplay.server.service.PartidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartidoController.class)
class PartidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PartidoService partidoService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void terminarPartido_tokenValido_noRequiereResultado() throws Exception {
        PartidoDto dto = new PartidoDto();
        dto.setId(9L);
        dto.setTerminado(true);
        dto.setFechaHora(LocalDateTime.now());

        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(100L);
        when(partidoService.terminarPartido(9L, 100L)).thenReturn(dto);

        mockMvc.perform(post("/api/partidos/9/terminar")
                        .header("Authorization", "Bearer token-ok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.terminado").value(true));
    }

    @Test
    void terminarPartido_tokenInvalido_devuelve401() throws Exception {
        when(jwtService.validarToken("token-bad")).thenReturn(false);

        mockMvc.perform(post("/api/partidos/9/terminar")
                        .header("Authorization", "Bearer token-bad"))
                .andExpect(status().isUnauthorized());
    }
}
