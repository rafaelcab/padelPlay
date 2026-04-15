package com.padelplay.server.service;

import com.padelplay.common.dto.PartidoDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartidoServiceTest {

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private PerfilJugadorRepository perfilJugadorRepository;

    @InjectMocks
    private PartidoService partidoService;

    @Test
    void crearPartido_debeFallarSiNoHayCreador() {
        PartidoDto dto = baseDto();
        dto.setCreador(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> partidoService.crearPartido(dto));

        assertTrue(ex.getMessage().contains("perfil de creador"));
    }

    @Test
    void crearPartido_debeFallarSiCreadorNoExiste() {
        PartidoDto dto = baseDto();
        dto.setCreador(perfilDto(777L));
        when(perfilJugadorRepository.findById(777L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> partidoService.crearPartido(dto));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void crearPartido_debeFallarSiNivelFueraDeRango() {
        PerfilJugador creador = perfil(1L, "creador", 3.2);
        PartidoDto dto = baseDto();
        dto.setCreador(perfilDto(1L));
        dto.setNivelRequerido(5.5);
        when(perfilJugadorRepository.findById(1L)).thenReturn(Optional.of(creador));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> partidoService.crearPartido(dto));

        assertTrue(ex.getMessage().contains("entre 1.0 y 5.0"));
    }

    @Test
    void crearPartido_debeFallarSiFechaEsPasada() {
        PerfilJugador creador = perfil(1L, "creador", 3.2);
        PartidoDto dto = baseDto();
        dto.setCreador(perfilDto(1L));
        dto.setFechaHora(LocalDateTime.now().minusHours(1));
        when(perfilJugadorRepository.findById(1L)).thenReturn(Optional.of(creador));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> partidoService.crearPartido(dto));

        assertTrue(ex.getMessage().contains("futuro"));
    }

    @Test
    void crearPartido_privadoDebeAsignarCodigoYCreadorComoPrimerJugador() {
        PerfilJugador creador = perfil(1L, "creador", 3.2);
        PartidoDto dto = baseDto();
        dto.setTipoPartido("private");
        dto.setCreador(perfilDto(1L));

        when(perfilJugadorRepository.findById(1L)).thenReturn(Optional.of(creador));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> {
            Partido partido = invocation.getArgument(0);
            partido.setId(999L);
            return partido;
        });

        PartidoDto resultado = partidoService.crearPartido(dto);

        ArgumentCaptor<Partido> captor = ArgumentCaptor.forClass(Partido.class);
        verify(partidoRepository).save(captor.capture());
        Partido guardado = captor.getValue();

        assertEquals("PRIVADO", guardado.getTipoPartido());
        assertNotNull(guardado.getCodigoAcceso());
        assertEquals(6, guardado.getCodigoAcceso().length());
        assertEquals(3, guardado.getHuecosDisponibles());
        assertEquals(1, guardado.getJugadoresApuntados().size());
        assertEquals(1L, guardado.getCreador().getId());

        assertEquals("PRIVADO", resultado.getTipoPartido());
        assertNotNull(resultado.getCodigoAcceso());
        assertEquals(1, resultado.getJugadoresApuntados().size());
    }

    @Test
    void crearPartido_publicoNoDebeTenerCodigo() {
        PerfilJugador creador = perfil(1L, "creador", 2.9);
        PartidoDto dto = baseDto();
        dto.setTipoPartido("public");
        dto.setCreador(perfilDto(1L));

        when(perfilJugadorRepository.findById(1L)).thenReturn(Optional.of(creador));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> {
            Partido partido = invocation.getArgument(0);
            partido.setId(999L);
            return partido;
        });

        PartidoDto resultado = partidoService.crearPartido(dto);

        assertEquals("ABIERTO", resultado.getTipoPartido());
        assertNull(resultado.getCodigoAcceso());
    }

    @Test
    void listarPartidos_debeMapearListaAListaDto() {
        PerfilJugador creador = perfil(1L, "ana", 3.7);
        Partido partido = new Partido();
        partido.setId(10L);
        partido.setFechaHora(LocalDateTime.now().plusDays(2));
        partido.setUbicacion("Bilbao");
        partido.setTipoPartido("ABIERTO");
        partido.setNivelRequerido(3.0);
        partido.setHuecosDisponibles(2);
        partido.setCodigoAcceso(null);
        partido.setCreador(creador);
        partido.setJugadoresApuntados(new ArrayList<>(List.of(creador)));

        when(partidoRepository.findAll()).thenReturn(List.of(partido));

        List<PartidoDto> resultado = partidoService.listarPartidos();

        assertEquals(1, resultado.size());
        PartidoDto dto = resultado.get(0);
        assertEquals(10L, dto.getId());
        assertEquals("Bilbao", dto.getUbicacion());
        assertEquals("ABIERTO", dto.getTipoPartido());
        assertEquals(1, dto.getJugadoresApuntados().size());
        assertEquals(1L, dto.getCreador().getId());
        assertEquals("ana", dto.getCreador().getApodo());
    }

    @Test
    void listarPartidos_debeEliminarPartidosSinJugadoresYNoDevolverlos() {
        PerfilJugador creador = perfil(1L, "ana", 3.7);

        Partido partidoValido = new Partido();
        partidoValido.setId(10L);
        partidoValido.setFechaHora(LocalDateTime.now().plusDays(2));
        partidoValido.setUbicacion("Bilbao");
        partidoValido.setTipoPartido("ABIERTO");
        partidoValido.setNivelRequerido(3.0);
        partidoValido.setHuecosDisponibles(2);
        partidoValido.setCreador(creador);
        partidoValido.setJugadoresApuntados(new ArrayList<>(List.of(creador)));

        Partido partidoSinJugadores = new Partido();
        partidoSinJugadores.setId(11L);
        partidoSinJugadores.setFechaHora(LocalDateTime.now().plusDays(2));
        partidoSinJugadores.setUbicacion("Donostia");
        partidoSinJugadores.setTipoPartido("ABIERTO");
        partidoSinJugadores.setNivelRequerido(2.5);
        partidoSinJugadores.setHuecosDisponibles(3);
        partidoSinJugadores.setCreador(creador);
        partidoSinJugadores.setJugadoresApuntados(new ArrayList<>());

        when(partidoRepository.findAll()).thenReturn(List.of(partidoValido, partidoSinJugadores));

        List<PartidoDto> resultado = partidoService.listarPartidos();

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getId());
        verify(partidoRepository).deleteAll(List.of(partidoSinJugadores));
    }

    @Test
    void unirseAPartido_debeFallarSiPartidoNoExiste() {
        when(partidoRepository.findById(11L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> partidoService.unirseAPartido(11L, 2L, null));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void unirseAPartido_debeFallarSiJugadorNoExiste() {
        Partido partido = partidoAbiertoConCreador(1L);
        when(partidoRepository.findById(99L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findById(22L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> partidoService.unirseAPartido(99L, 22L, null));

        assertTrue(ex.getMessage().contains("jugador no existe"));
    }

    @Test
    void unirseAPartido_debeFallarSiNoHayHuecos() {
        Partido partido = partidoAbiertoConCreador(1L);
        partido.setHuecosDisponibles(0);

        when(partidoRepository.findById(2L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findById(8L)).thenReturn(Optional.of(perfil(8L, "pepe", 2.3)));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> partidoService.unirseAPartido(2L, 8L, null));

        assertTrue(ex.getMessage().contains("completo"));
    }

    @Test
    void unirseAPartido_debeFallarSiEsElCreador() {
        Partido partido = partidoAbiertoConCreador(1L);

        when(partidoRepository.findById(3L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findById(1L)).thenReturn(Optional.of(perfil(1L, "ana", 3.5)));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> partidoService.unirseAPartido(3L, 1L, null));

        assertTrue(ex.getMessage().contains("creador"));
    }

    @Test
    void unirseAPartido_debeFallarSiYaApuntado() {
        PerfilJugador jugador = perfil(2L, "pepe", 3.0);
        Partido partido = partidoAbiertoConCreador(1L);
        partido.getJugadoresApuntados().add(jugador);

        when(partidoRepository.findById(4L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findById(2L)).thenReturn(Optional.of(jugador));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> partidoService.unirseAPartido(4L, 2L, null));

        assertTrue(ex.getMessage().contains("Ya estás apuntado"));
    }

    @Test
    void unirseAPartido_privadoDebeFallarConCodigoIncorrecto() {
        Partido partido = partidoPrivadoConCreador(1L, "ABC123");
        PerfilJugador jugador = perfil(3L, "laura", 3.4);

        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findById(3L)).thenReturn(Optional.of(jugador));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> partidoService.unirseAPartido(5L, 3L, "XXX999"));

        assertTrue(ex.getMessage().contains("Código de acceso"));
    }

    @Test
    void unirseAPartido_privadoDebeAceptarCodigoCorrectoIgnorandoMayusculas() {
        Partido partido = partidoPrivadoConCreador(1L, "ABC123");
        PerfilJugador jugador = perfil(7L, "laura", 3.4);

        when(partidoRepository.findById(6L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findById(7L)).thenReturn(Optional.of(jugador));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartidoDto resultado = partidoService.unirseAPartido(6L, 7L, "abc123");

        assertEquals(2, resultado.getJugadoresApuntados().size());
        assertEquals(2, resultado.getHuecosDisponibles());
    }

    @Test
    void cancelarAsistencia_jugadorNormalDebeEliminarseDeLaLista() {
        PerfilJugador creador = perfil(1L, "creador", 3.0);
        PerfilJugador jugador = perfil(2L, "pepe", 3.0);
        Partido partido = partidoAbiertoConCreador(1L);
        partido.getJugadoresApuntados().add(jugador);
        partido.setHuecosDisponibles(2);

        when(partidoRepository.findById(50L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findById(2L)).thenReturn(Optional.of(jugador));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartidoDto resultado = partidoService.cancelarAsistencia(50L, 2L);

        assertEquals(1, resultado.getJugadoresApuntados().size());
        assertFalse(resultado.getJugadoresApuntados().stream().anyMatch(p -> p.getId().equals(2L)));
        assertEquals(3, resultado.getHuecosDisponibles());
        assertFalse(resultado.isCancelado());
    }

    @Test
    void cancelarAsistencia_creadorSoloDebeEliminarPartido() {
        Partido partido = partidoAbiertoConCreador(1L);

        when(partidoRepository.findById(60L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findById(1L)).thenReturn(Optional.of(perfil(1L, "creador", 3.0)));

        PartidoDto resultado = partidoService.cancelarAsistencia(60L, 1L);

        assertEquals(0, resultado.getJugadoresApuntados().size());
        verify(partidoRepository).delete(partido);
    }

    @Test
    void cancelarAsistencia_jugadorNoApuntadoDebeLanzarExcepcion() {
        Partido partido = partidoAbiertoConCreador(1L);

        when(partidoRepository.findById(70L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findById(9L)).thenReturn(Optional.of(perfil(9L, "mike", 2.8)));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> partidoService.cancelarAsistencia(70L, 9L));

        assertTrue(ex.getMessage().contains("no está apuntado"));
    }

    private PartidoDto baseDto() {
        PartidoDto dto = new PartidoDto();
        dto.setFechaHora(LocalDateTime.now().plusDays(1));
        dto.setUbicacion("Donostia");
        dto.setTipoPartido("public");
        dto.setNivelRequerido(2.5);
        return dto;
    }

    private PerfilJugadorDto perfilDto(Long id) {
        PerfilJugadorDto dto = new PerfilJugadorDto();
        dto.setId(id);
        return dto;
    }

    private PerfilJugador perfil(Long id, String apodo, Double nivel) {
        PerfilJugador perfil = new PerfilJugador();
        perfil.setId(id);
        perfil.setApodo(apodo);
        perfil.setNivel(nivel);
        return perfil;
    }

    private Partido partidoAbiertoConCreador(Long creadorId) {
        PerfilJugador creador = perfil(creadorId, "creador", 3.0);

        Partido partido = new Partido();
        partido.setId(100L);
        partido.setFechaHora(LocalDateTime.now().plusDays(1));
        partido.setUbicacion("Bilbao");
        partido.setTipoPartido("ABIERTO");
        partido.setNivelRequerido(3.0);
        partido.setHuecosDisponibles(3);
        partido.setCreador(creador);
        partido.setJugadoresApuntados(new ArrayList<>(List.of(creador)));
        return partido;
    }

    private Partido partidoPrivadoConCreador(Long creadorId, String codigo) {
        Partido partido = partidoAbiertoConCreador(creadorId);
        partido.setTipoPartido("PRIVADO");
        partido.setCodigoAcceso(codigo);
        return partido;
    }
}
