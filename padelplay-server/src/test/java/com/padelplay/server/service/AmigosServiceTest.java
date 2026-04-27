package com.padelplay.server.service;

import com.padelplay.common.dto.PartidoJugadoPublicoDto;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.PerfilEntrenadorRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.SeguimientoAmigoRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmigosServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilJugadorRepository perfilJugadorRepository;

    @Mock
    private PerfilEntrenadorRepository perfilEntrenadorRepository;

    @Mock
    private SeguimientoAmigoRepository seguimientoAmigoRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @InjectMocks
    private AmigosService amigosService;

    @Test
    void listarPartidosJugadosPublicos_debeLanzarErrorSiUsuarioObjetivoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> amigosService.listarPartidosJugadosPublicos(99L));

        assertEquals("El perfil solicitado no existe.", ex.getMessage());
    }

    @Test
    void listarPartidosJugadosPublicos_debeDevolverListaVaciaSiUsuarioNoTienePerfilJugador() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario(10L, "Luis")));
        when(perfilJugadorRepository.findByUsuarioId(10L)).thenReturn(Optional.empty());

        List<PartidoJugadoPublicoDto> resultado = amigosService.listarPartidosJugadosPublicos(10L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarPartidosJugadosPublicos_debeFiltrarPartidosInvalidosYOrdenarPorFechaDesc() {
        Usuario usuarioObjetivo = usuario(20L, "Ana");
        PerfilJugador perfilObjetivo = perfil(5L, "anita", usuarioObjetivo);
        PerfilJugador otroCreador = perfil(8L, "pepe", usuario(30L, "Pepe"));

        Partido validoMasAntiguo = partido(100L, LocalDateTime.now().minusDays(5), otroCreador, false, true, "Bilbao", "ABIERTO");
        Partido cancelado = partido(101L, LocalDateTime.now().minusDays(2), otroCreador, true, true, "Madrid", "ABIERTO");
        Partido noTerminado = partido(102L, LocalDateTime.now().minusDays(1), otroCreador, false, false, "Valencia", "PRIVADO");
        Partido validoMasReciente = partido(103L, LocalDateTime.now().minusHours(3), otroCreador, false, true, "Sevilla", "ABIERTO");

        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuarioObjetivo));
        when(perfilJugadorRepository.findByUsuarioId(20L)).thenReturn(Optional.of(perfilObjetivo));
        when(partidoRepository.findPartidosTerminadosNoCanceladosByJugadorId(5L))
                .thenReturn(List.of(validoMasAntiguo, cancelado, noTerminado, validoMasReciente));

        List<PartidoJugadoPublicoDto> resultado = amigosService.listarPartidosJugadosPublicos(20L);

        assertEquals(2, resultado.size());
        assertEquals(103L, resultado.get(0).getPartidoId());
        assertEquals(100L, resultado.get(1).getPartidoId());
        assertEquals("Sevilla", resultado.get(0).getUbicacion());
        assertFalse(resultado.stream().anyMatch(dto -> dto.getPartidoId().equals(101L)));
        assertFalse(resultado.stream().anyMatch(dto -> dto.getPartidoId().equals(102L)));
    }

    @Test
    void listarPartidosJugadosPublicos_debeMapearCreadorYMarcarSiUsuarioObjetivoFueCreador() {
        Usuario usuarioObjetivo = usuario(40L, "Marta");
        PerfilJugador perfilObjetivo = perfil(12L, "martuki", usuarioObjetivo);

        Partido partidoCreadoPorObjetivo = partido(200L, LocalDateTime.now().minusDays(1), perfilObjetivo, false, true, "Granada", "PRIVADO");

        when(usuarioRepository.findById(40L)).thenReturn(Optional.of(usuarioObjetivo));
        when(perfilJugadorRepository.findByUsuarioId(40L)).thenReturn(Optional.of(perfilObjetivo));
        when(partidoRepository.findPartidosTerminadosNoCanceladosByJugadorId(12L))
                .thenReturn(List.of(partidoCreadoPorObjetivo));

        List<PartidoJugadoPublicoDto> resultado = amigosService.listarPartidosJugadosPublicos(40L);

        assertEquals(1, resultado.size());
        PartidoJugadoPublicoDto dto = resultado.get(0);
        assertEquals(12L, dto.getCreadorId());
        assertEquals("martuki", dto.getCreadorApodo());
        assertTrue(dto.isUsuarioObjetivoFueCreador());
        verify(partidoRepository).findPartidosTerminadosNoCanceladosByJugadorId(12L);
    }

    private Usuario usuario(Long id, String nombre) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(nombre.toLowerCase() + "@mail.com");
        return usuario;
    }

    private PerfilJugador perfil(Long id, String apodo, Usuario usuario) {
        PerfilJugador perfil = new PerfilJugador();
        perfil.setId(id);
        perfil.setApodo(apodo);
        perfil.setUsuario(usuario);
        return perfil;
    }

    private Partido partido(Long id,
                            LocalDateTime fechaHora,
                            PerfilJugador creador,
                            boolean cancelado,
                            boolean terminado,
                            String ubicacion,
                            String tipoPartido) {
        Partido partido = new Partido();
        partido.setId(id);
        partido.setFechaHora(fechaHora);
        partido.setCreador(creador);
        partido.setCancelado(cancelado);
        partido.setTerminado(terminado);
        partido.setUbicacion(ubicacion);
        partido.setTipoPartido(tipoPartido);
        return partido;
    }
}
