package com.padelplay.server.facade;

import com.padelplay.common.dto.PartidoJugadoPublicoDto;
import com.padelplay.common.dto.PartidosJugadosPublicosCursorDto;
import com.padelplay.server.service.AmigosService;
import com.padelplay.server.service.EntidadNoEncontradaException;
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
        PartidoJugadoPublicoDto item = new PartidoJugadoPublicoDto();
        item.setPartidoId(1L);
        item.setFechaHora(LocalDateTime.now().minusDays(1));
        item.setUbicacion("Bilbao");

        PartidosJugadosPublicosCursorDto dto = new PartidosJugadosPublicosCursorDto();
        dto.setItems(List.of(item));
        dto.setHasNext(true);
        dto.setNextCursor("cursor-2");

        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(5L);
        when(amigosService.listarPartidosJugadosPublicos(9L, 3, null, "next")).thenReturn(dto);

        ResponseEntity<?> response = amigosController.listarPartidosJugadosPublicos("Bearer token-ok", 9L, 3, null, "next");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(PartidosJugadosPublicosCursorDto.class, response.getBody());
        PartidosJugadosPublicosCursorDto body = (PartidosJugadosPublicosCursorDto) response.getBody();
        assertEquals(1, body.getItems().size());
        assertTrue(body.isHasNext());
    }

    @Test
    void listarPartidosJugadosPublicos_debeResponder404CuandoElJugadorNoExiste() {
        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(5L);
        when(amigosService.listarPartidosJugadosPublicos(99L, 10, null, "next"))
                .thenThrow(new EntidadNoEncontradaException("El perfil solicitado no existe."));

        ResponseEntity<?> response = amigosController.listarPartidosJugadosPublicos("Bearer token-ok", 99L, 10, null, "next");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("El perfil solicitado no existe.", body.get("error"));
    }

    @Test
    void listarPartidosJugadosPublicos_debeResponder400CuandoElCursorEsInvalido() {
        when(jwtService.validarToken("token-ok")).thenReturn(true);
        when(jwtService.extraerUsuarioId("token-ok")).thenReturn(5L);
        when(amigosService.listarPartidosJugadosPublicos(12L, 10, "bad", "next"))
                .thenThrow(new IllegalArgumentException("Cursor invalido."));

        ResponseEntity<?> response = amigosController.listarPartidosJugadosPublicos("Bearer token-ok", 12L, 10, "bad", "next");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Cursor invalido.", body.get("error"));
    }

    @Test
    void listarPartidosJugadosPublicos_debeResponder401CuandoFaltaToken() {
        ResponseEntity<?> response = amigosController.listarPartidosJugadosPublicos(null, 7L, 3, null, "next");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(String.valueOf(body.get("error")).contains("Token"));
    }
}
