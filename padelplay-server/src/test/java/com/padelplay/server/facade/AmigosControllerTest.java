package com.padelplay.server.facade;

import com.padelplay.common.dto.PartidoJugadoPublicoDto;
import com.padelplay.server.service.AmigosService;
import com.padelplay.server.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmigosControllerTest {

    @Mock
    private AmigosService amigosService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AmigosController amigosController;

    @Test
    void listarPartidosJugadosPublicos_debeResponder200CuandoElJugadorEsValido() {
        PartidoJugadoPublicoDto dto = new PartidoJugadoPublicoDto();
        dto.setPartidoId(1L);
        dto.setFechaHora(LocalDateTime.now().minusDays(1));
        dto.setUbicacion("Bilbao");

        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(5L);
        when(amigosService.listarPartidosJugadosPublicos(9L)).thenReturn(List.of(dto));

        ResponseEntity<?> response = amigosController.listarPartidosJugadosPublicos("Bearer token-ok", 9L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        List<?> body = (List<?>) response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void listarPartidosJugadosPublicos_debeResponder404CuandoElJugadorNoExiste() {
        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(5L);
        when(amigosService.listarPartidosJugadosPublicos(99L))
                .thenThrow(new IllegalArgumentException("El perfil solicitado no existe."));

        ResponseEntity<?> response = amigosController.listarPartidosJugadosPublicos("Bearer token-ok", 99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("El perfil solicitado no existe.", body.get("error"));
    }

    @Test
    void listarPartidosJugadosPublicos_debeResponder401CuandoFaltaToken() {
        ResponseEntity<?> response = amigosController.listarPartidosJugadosPublicos(null, 7L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(String.valueOf(body.get("error")).contains("Token"));
    }
}
