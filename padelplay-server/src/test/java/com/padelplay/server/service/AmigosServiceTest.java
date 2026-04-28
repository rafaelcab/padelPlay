package com.padelplay.server.service;

import com.padelplay.common.dto.PartidoJugadoPublicoDto;
import com.padelplay.common.dto.PartidosJugadosPublicosCursorDto;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.ResultadoPartido;
import com.padelplay.server.entity.TipoFinalizacionResultadoPartido;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.PerfilEntrenadorRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.ResultadoPartidoRepository;
import com.padelplay.server.repository.SeguimientoAmigoRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
    private ResultadoPartidoRepository resultadoPartidoRepository;

    @Mock
    private TrayectoriaCursorService trayectoriaCursorService;

    @InjectMocks
    private AmigosService amigosService;

    @Test
    void listarPartidosJugadosPublicos_debeLanzarErrorSiUsuarioObjetivoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        EntidadNoEncontradaException ex = assertThrows(EntidadNoEncontradaException.class,
                () -> amigosService.listarPartidosJugadosPublicos(99L, 3, null, "next"));

        assertEquals("El perfil solicitado no existe.", ex.getMessage());
    }

    @Test
    void listarPartidosJugadosPublicos_debeDevolverListaVaciaSiUsuarioNoTienePerfilJugador() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario(10L, "Luis")));
        when(perfilJugadorRepository.findByUsuarioId(10L)).thenReturn(Optional.empty());

        PartidosJugadosPublicosCursorDto resultado = amigosService.listarPartidosJugadosPublicos(10L, 3, null, "next");

        assertTrue(resultado.getItems().isEmpty());
        assertFalse(resultado.isHasNext());
        assertFalse(resultado.isHasPrevious());
        assertEquals(null, resultado.getNextCursor());
        assertEquals(null, resultado.getPreviousCursor());
    }

    @Test
    void listarPartidosJugadosPublicos_debeDevolverPreviewOrdenadaYMapeada() {
        Usuario usuarioObjetivo = usuario(20L, "Ana");
        PerfilJugador perfilObjetivo = perfil(5L, "anita", usuarioObjetivo);
        PerfilJugador companeroA = perfil(6L, "pablo", usuario(21L, "Pablo"));
        PerfilJugador rival1 = perfil(7L, "marcos", usuario(22L, "Marcos"));
        PerfilJugador rival2 = perfil(8L, "lucas", usuario(23L, "Lucas"));
        PerfilJugador otroCreador = perfil(9L, "pepe", usuario(24L, "Pepe"));

        ResultadoPartido reciente = resultado(
                103L,
                LocalDateTime.of(2026, 4, 27, 20, 0),
                otroCreador,
                perfilObjetivo,
                companeroA,
                rival1,
                rival2,
                TipoFinalizacionResultadoPartido.FINALIZADO_NORMAL,
                6,
                4
        );
        ResultadoPartido antiguo = resultado(
                100L,
                LocalDateTime.of(2026, 4, 20, 18, 0),
                perfilObjetivo,
                perfilObjetivo,
                companeroA,
                rival1,
                rival2,
                TipoFinalizacionResultadoPartido.WO_EQUIPO_B,
                null,
                null
        );

        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuarioObjetivo));
        when(perfilJugadorRepository.findByUsuarioId(20L)).thenReturn(Optional.of(perfilObjetivo));
        when(trayectoriaCursorService.decodificar(null)).thenReturn(null);
        when(resultadoPartidoRepository.findTrayectoriaPublicaInicial(eq(5L), any(Pageable.class)))
                .thenReturn(List.of(reciente, antiguo));

        PartidosJugadosPublicosCursorDto resultado = amigosService.listarPartidosJugadosPublicos(20L, 3, null, "next");

        assertEquals(2, resultado.getItems().size());
        assertFalse(resultado.isHasNext());
        assertFalse(resultado.isHasPrevious());

        PartidoJugadoPublicoDto primero = resultado.getItems().get(0);
        assertEquals(103L, primero.getPartidoId());
        assertEquals("FINALIZADO_NORMAL", primero.getTipoFinalizacion());
        assertEquals(6, primero.getJuegosEquipoA());
        assertEquals(4, primero.getJuegosEquipoB());
        assertEquals("A", primero.getEquipoUsuarioObjetivo());
        assertTrue(primero.getEquipoA().get(0).isUsuarioObjetivo());
        assertEquals("pepe", primero.getCreadorApodo());
        assertFalse(primero.isUsuarioObjetivoFueCreador());

        PartidoJugadoPublicoDto segundo = resultado.getItems().get(1);
        assertEquals(100L, segundo.getPartidoId());
        assertEquals("WO_EQUIPO_B", segundo.getTipoFinalizacion());
        assertTrue(segundo.isUsuarioObjetivoFueCreador());
        assertEquals("anita", segundo.getCreadorApodo());
        verify(resultadoPartidoRepository).findTrayectoriaPublicaInicial(eq(5L), any(Pageable.class));
    }

    @Test
    void listarPartidosJugadosPublicos_debeConstruirCursorSiguienteSinRepeticiones() {
        Usuario usuarioObjetivo = usuario(30L, "Marta");
        PerfilJugador perfilObjetivo = perfil(12L, "martuki", usuarioObjetivo);
        PerfilJugador companero = perfil(13L, "alba", usuario(31L, "Alba"));
        PerfilJugador rival1 = perfil(14L, "raul", usuario(32L, "Raul"));
        PerfilJugador rival2 = perfil(15L, "luis", usuario(33L, "Luis"));
        TrayectoriaCursorService.TrayectoriaCursor cursor = new TrayectoriaCursorService.TrayectoriaCursor(
                LocalDateTime.of(2026, 4, 26, 10, 0),
                300L
        );

        ResultadoPartido pagina1 = resultado(299L, LocalDateTime.of(2026, 4, 25, 10, 0), perfilObjetivo, perfilObjetivo, companero, rival1, rival2, TipoFinalizacionResultadoPartido.FINALIZADO_NORMAL, 6, 3);
        ResultadoPartido pagina2 = resultado(298L, LocalDateTime.of(2026, 4, 24, 10, 0), perfilObjetivo, perfilObjetivo, companero, rival1, rival2, TipoFinalizacionResultadoPartido.FINALIZADO_NORMAL, 6, 2);
        ResultadoPartido extra = resultado(297L, LocalDateTime.of(2026, 4, 23, 10, 0), perfilObjetivo, perfilObjetivo, companero, rival1, rival2, TipoFinalizacionResultadoPartido.FINALIZADO_NORMAL, 6, 0);

        when(usuarioRepository.findById(30L)).thenReturn(Optional.of(usuarioObjetivo));
        when(perfilJugadorRepository.findByUsuarioId(30L)).thenReturn(Optional.of(perfilObjetivo));
        when(trayectoriaCursorService.decodificar("cursor-next")).thenReturn(cursor);
        when(resultadoPartidoRepository.findTrayectoriaPublicaNext(eq(12L), eq(cursor.fechaHora()), eq(cursor.partidoId()), any(Pageable.class)))
                .thenReturn(List.of(pagina1, pagina2, extra));
        when(trayectoriaCursorService.codificar(pagina1.getPartido().getFechaHora(), pagina1.getPartido().getId()))
                .thenReturn("cursor-prev-page");
        when(trayectoriaCursorService.codificar(pagina2.getPartido().getFechaHora(), pagina2.getPartido().getId()))
                .thenReturn("cursor-next-page");

        PartidosJugadosPublicosCursorDto resultado = amigosService.listarPartidosJugadosPublicos(30L, 2, "cursor-next", "next");

        assertEquals(2, resultado.getItems().size());
        assertTrue(resultado.isHasNext());
        assertTrue(resultado.isHasPrevious());
        assertEquals("cursor-next-page", resultado.getNextCursor());
        assertEquals("cursor-prev-page", resultado.getPreviousCursor());
        assertEquals(299L, resultado.getItems().get(0).getPartidoId());
        assertEquals(298L, resultado.getItems().get(1).getPartidoId());
    }

    @Test
    void listarPartidosJugadosPublicos_debeReconstruirPaginaAnteriorEnOrdenDesc() {
        Usuario usuarioObjetivo = usuario(40L, "Nora");
        PerfilJugador perfilObjetivo = perfil(50L, "nora", usuarioObjetivo);
        PerfilJugador companero = perfil(51L, "ivan", usuario(41L, "Ivan"));
        PerfilJugador rival1 = perfil(52L, "saul", usuario(42L, "Saul"));
        PerfilJugador rival2 = perfil(53L, "javi", usuario(43L, "Javi"));
        TrayectoriaCursorService.TrayectoriaCursor cursor = new TrayectoriaCursorService.TrayectoriaCursor(
                LocalDateTime.of(2026, 4, 24, 10, 0),
                400L
        );

        ResultadoPartido masAntiguoAsc = resultado(401L, LocalDateTime.of(2026, 4, 25, 9, 0), perfilObjetivo, perfilObjetivo, companero, rival1, rival2, TipoFinalizacionResultadoPartido.WO_EQUIPO_A, null, null);
        ResultadoPartido masRecienteAsc = resultado(402L, LocalDateTime.of(2026, 4, 26, 9, 0), perfilObjetivo, perfilObjetivo, companero, rival1, rival2, TipoFinalizacionResultadoPartido.WO_EQUIPO_A, null, null);
        ResultadoPartido extraAsc = resultado(403L, LocalDateTime.of(2026, 4, 27, 9, 0), perfilObjetivo, perfilObjetivo, companero, rival1, rival2, TipoFinalizacionResultadoPartido.WO_EQUIPO_A, null, null);

        when(usuarioRepository.findById(40L)).thenReturn(Optional.of(usuarioObjetivo));
        when(perfilJugadorRepository.findByUsuarioId(40L)).thenReturn(Optional.of(perfilObjetivo));
        when(trayectoriaCursorService.decodificar("cursor-prev")).thenReturn(cursor);
        when(resultadoPartidoRepository.findTrayectoriaPublicaPrevious(eq(50L), eq(cursor.fechaHora()), eq(cursor.partidoId()), any(Pageable.class)))
                .thenReturn(List.of(masAntiguoAsc, masRecienteAsc, extraAsc));
        when(trayectoriaCursorService.codificar(masRecienteAsc.getPartido().getFechaHora(), masRecienteAsc.getPartido().getId()))
                .thenReturn("cursor-prev-again");
        when(trayectoriaCursorService.codificar(masAntiguoAsc.getPartido().getFechaHora(), masAntiguoAsc.getPartido().getId()))
                .thenReturn("cursor-next-again");

        PartidosJugadosPublicosCursorDto resultado = amigosService.listarPartidosJugadosPublicos(40L, 2, "cursor-prev", "previous");

        assertEquals(2, resultado.getItems().size());
        assertTrue(resultado.isHasNext());
        assertTrue(resultado.isHasPrevious());
        assertEquals(402L, resultado.getItems().get(0).getPartidoId());
        assertEquals(401L, resultado.getItems().get(1).getPartidoId());
        assertEquals("cursor-prev-again", resultado.getPreviousCursor());
        assertEquals("cursor-next-again", resultado.getNextCursor());
    }

    @Test
    void listarPartidosJugadosPublicos_debeRechazarCursorInvalido() {
        when(usuarioRepository.findById(60L)).thenReturn(Optional.of(usuario(60L, "Leo")));
        when(perfilJugadorRepository.findByUsuarioId(60L)).thenReturn(Optional.of(perfil(70L, "leo", usuario(60L, "Leo"))));
        when(trayectoriaCursorService.decodificar("mal")).thenThrow(new IllegalArgumentException("Cursor invalido."));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> amigosService.listarPartidosJugadosPublicos(60L, 3, "mal", "next"));

        assertEquals("Cursor invalido.", ex.getMessage());
        verify(resultadoPartidoRepository, never()).findTrayectoriaPublicaInicial(any(), any());
    }

    @Test
    void listarPartidosJugadosPublicos_debeRechazarLimitMayorAlMaximo() {
        when(usuarioRepository.findById(70L)).thenReturn(Optional.of(usuario(70L, "Pau")));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> amigosService.listarPartidosJugadosPublicos(70L, 11, null, "next"));

        assertEquals("El limite solicitado no es valido.", ex.getMessage());
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

    private ResultadoPartido resultado(Long partidoId,
                                       LocalDateTime fechaHora,
                                       PerfilJugador creador,
                                       PerfilJugador equipoA1,
                                       PerfilJugador equipoA2,
                                       PerfilJugador equipoB1,
                                       PerfilJugador equipoB2,
                                       TipoFinalizacionResultadoPartido tipoFinalizacion,
                                       Integer juegosEquipoA,
                                       Integer juegosEquipoB) {
        Partido partido = new Partido();
        partido.setId(partidoId);
        partido.setFechaHora(fechaHora);
        partido.setCreador(creador);
        partido.setUbicacion("Club " + partidoId);
        partido.setTipoPartido("ABIERTO");
        partido.setCancelado(false);
        partido.setTerminado(true);

        ResultadoPartido resultado = new ResultadoPartido();
        resultado.setPartido(partido);
        resultado.setRegistradoPor(creador);
        resultado.setEquipoAJugador1(equipoA1);
        resultado.setEquipoAJugador2(equipoA2);
        resultado.setEquipoBJugador1(equipoB1);
        resultado.setEquipoBJugador2(equipoB2);
        resultado.setTipoFinalizacion(tipoFinalizacion);
        resultado.setJuegosEquipoA(juegosEquipoA);
        resultado.setJuegosEquipoB(juegosEquipoB);
        return resultado;
    }
}
