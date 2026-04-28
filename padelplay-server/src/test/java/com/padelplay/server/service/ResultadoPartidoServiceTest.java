package com.padelplay.server.service;

import com.padelplay.common.dto.RegistrarResultadoPartidoRequestDto;
import com.padelplay.common.dto.ResultadoPartidoGestionCreadorDto;
import com.padelplay.common.dto.ResultadoPartidoPendienteValidacionDto;
import com.padelplay.common.dto.ResultadoPartidoDto;
import com.padelplay.common.dto.ValidarResultadoPartidoRequestDto;
import com.padelplay.server.entity.EstadoValidacionResultadoPartido;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.ResultadoPartido;
import com.padelplay.server.entity.TipoFinalizacionResultadoPartido;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.entity.ValidacionResultadoPartido;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.ResultadoPartidoRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultadoPartidoServiceTest {

    @Mock
    private ResultadoPartidoRepository resultadoPartidoRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private PerfilJugadorRepository perfilJugadorRepository;

    @InjectMocks
    private ResultadoPartidoService resultadoPartidoService;

    @Test
    void registrarResultado_partidoValidoRegistraEquiposMarcaTerminadoYPendienteValidacion() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);
        Partido partido = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);

        when(partidoRepository.findById(50L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findByUsuarioId(101L)).thenReturn(Optional.of(creador));
        when(resultadoPartidoRepository.findByPartidoIdWithDetalles(50L)).thenReturn(Optional.empty());
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(resultadoPartidoRepository.save(any(ResultadoPartido.class))).thenAnswer(invocation -> {
            ResultadoPartido resultado = invocation.getArgument(0);
            resultado.setId(800L);
            resultado.setFechaRegistro(LocalDateTime.now());
            return resultado;
        });

        ResultadoPartidoDto dto = resultadoPartidoService.registrarResultado(50L, 101L, requestValido());

        ArgumentCaptor<ResultadoPartido> captor = ArgumentCaptor.forClass(ResultadoPartido.class);
        verify(resultadoPartidoRepository).save(captor.capture());
        ResultadoPartido guardado = captor.getValue();

        assertTrue(partido.isTerminado());
        assertEquals(1L, guardado.getRegistradoPor().getId());
        assertEquals(1L, guardado.getEquipoAJugador1().getId());
        assertEquals(2L, guardado.getEquipoAJugador2().getId());
        assertEquals(3L, guardado.getEquipoBJugador1().getId());
        assertEquals(4L, guardado.getEquipoBJugador2().getId());
        assertEquals(EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION, guardado.getEstadoValidacion());
        assertEquals("PENDIENTE_VALIDACION", dto.getEstadoValidacion());
        assertEquals(3, dto.getValidacionesPendientes());
        assertTrue(dto.isPartidoTerminado());
    }

    @Test
    void registrarResultado_debeFallarSiSolicitanteNoEsElCreador() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);
        Partido partido = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);

        when(partidoRepository.findById(50L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findByUsuarioId(102L)).thenReturn(Optional.of(jugador2));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> resultadoPartidoService.registrarResultado(50L, 102L, requestValido()));

        assertTrue(ex.getMessage().contains("Solo el creador"));
        verify(resultadoPartidoRepository, never()).save(any(ResultadoPartido.class));
    }

    @Test
    void registrarResultado_debeFallarSiLosEquiposNoCoincidenConLosParticipantes() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);
        Partido partido = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);

        RegistrarResultadoPartidoRequestDto request = requestValido();
        request.setEquipoBJugadorIds(List.of(3L, 99L));

        when(partidoRepository.findById(50L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findByUsuarioId(101L)).thenReturn(Optional.of(creador));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> resultadoPartidoService.registrarResultado(50L, 101L, request));

        assertTrue(ex.getMessage().contains("participantes del partido"));
    }

    @Test
    void registrarResultado_resultadoRechazadoPermiteReenvioYBorraValidacionesPrevias() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);
        Partido partido = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);

        ResultadoPartido resultadoExistente = resultadoExistente(partido, creador, EstadoValidacionResultadoPartido.RECHAZADO);
        ValidacionResultadoPartido rechazo = validacion(resultadoExistente, jugador2, false);
        resultadoExistente.getValidaciones().add(rechazo);

        when(partidoRepository.findById(50L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findByUsuarioId(101L)).thenReturn(Optional.of(creador));
        when(resultadoPartidoRepository.findByPartidoIdWithDetalles(50L)).thenReturn(Optional.of(resultadoExistente));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(resultadoPartidoRepository.save(any(ResultadoPartido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResultadoPartidoDto dto = resultadoPartidoService.registrarResultado(50L, 101L, requestValido());

        assertEquals("PENDIENTE_VALIDACION", dto.getEstadoValidacion());
        assertEquals(0, resultadoExistente.getValidaciones().size());
    }

    @Test
    void validarResultado_terceraAceptacionMarcaResultadoComoValidado() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);
        Partido partido = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);

        ResultadoPartido resultado = resultadoExistente(partido, creador, EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION);
        resultado.getValidaciones().add(validacion(resultado, jugador2, true));
        resultado.getValidaciones().add(validacion(resultado, jugador3, true));

        ValidarResultadoPartidoRequestDto request = new ValidarResultadoPartidoRequestDto();
        request.setAceptado(true);

        when(partidoRepository.findById(50L)).thenReturn(Optional.of(partido));
        when(resultadoPartidoRepository.findByPartidoIdWithDetalles(50L)).thenReturn(Optional.of(resultado));
        when(perfilJugadorRepository.findByUsuarioId(104L)).thenReturn(Optional.of(jugador4));
        when(resultadoPartidoRepository.save(any(ResultadoPartido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResultadoPartidoDto dto = resultadoPartidoService.validarResultado(50L, 104L, request);

        assertEquals("VALIDADO", dto.getEstadoValidacion());
        assertEquals(3, dto.getValidacionesAprobadas());
        assertEquals(0, dto.getValidacionesPendientes());
    }

    @Test
    void validarResultado_rechazoDeParticipanteMarcaResultadoComoRechazado() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);
        Partido partido = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);

        ResultadoPartido resultado = resultadoExistente(partido, creador, EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION);

        ValidarResultadoPartidoRequestDto request = new ValidarResultadoPartidoRequestDto();
        request.setAceptado(false);

        when(partidoRepository.findById(50L)).thenReturn(Optional.of(partido));
        when(resultadoPartidoRepository.findByPartidoIdWithDetalles(50L)).thenReturn(Optional.of(resultado));
        when(perfilJugadorRepository.findByUsuarioId(102L)).thenReturn(Optional.of(jugador2));
        when(resultadoPartidoRepository.save(any(ResultadoPartido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResultadoPartidoDto dto = resultadoPartidoService.validarResultado(50L, 102L, request);

        assertEquals("RECHAZADO", dto.getEstadoValidacion());
        assertEquals(1, dto.getValidacionesRechazadas());
        assertEquals(2, dto.getValidacionesPendientes());
    }

    @Test
    void validarResultado_debeFallarSiElMismoJugadorValidaDosVeces() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);
        Partido partido = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);

        ResultadoPartido resultado = resultadoExistente(partido, creador, EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION);
        resultado.getValidaciones().add(validacion(resultado, jugador2, true));

        ValidarResultadoPartidoRequestDto request = new ValidarResultadoPartidoRequestDto();
        request.setAceptado(true);

        when(partidoRepository.findById(50L)).thenReturn(Optional.of(partido));
        when(resultadoPartidoRepository.findByPartidoIdWithDetalles(50L)).thenReturn(Optional.of(resultado));
        when(perfilJugadorRepository.findByUsuarioId(102L)).thenReturn(Optional.of(jugador2));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> resultadoPartidoService.validarResultado(50L, 102L, request));

        assertTrue(ex.getMessage().contains("Ya has validado"));
    }

    @Test
    void obtenerResultado_debeFallarSiSolicitanteNoParticipaEnElPartido() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);
        PerfilJugador intruso = perfil(9L, "intruso", 109L);
        Partido partido = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);

        when(partidoRepository.findById(50L)).thenReturn(Optional.of(partido));
        when(perfilJugadorRepository.findByUsuarioId(109L)).thenReturn(Optional.of(intruso));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> resultadoPartidoService.obtenerResultado(50L, 109L));

        assertTrue(ex.getMessage().contains("participantes"));
        verify(resultadoPartidoRepository, never()).findByPartidoIdWithDetalles(50L);
    }

    @Test
    void listarPendientesValidacion_devuelveResultadosPendientesDelJugador() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);
        Partido partido = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);
        partido.setUbicacion("Club Norte");
        partido.setTipoPartido("ABIERTO");

        ResultadoPartido resultado = resultadoExistente(partido, creador, EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION);
        resultado.getValidaciones().add(validacion(resultado, jugador3, true));

        when(perfilJugadorRepository.findByUsuarioId(102L)).thenReturn(Optional.of(jugador2));
        when(resultadoPartidoRepository.findPendientesValidacionByPerfilJugadorId(
                2L,
                EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION
        )).thenReturn(List.of(resultado));

        List<ResultadoPartidoPendienteValidacionDto> pendientes =
                resultadoPartidoService.listarPendientesValidacion(102L);

        assertEquals(1, pendientes.size());
        assertEquals(50L, pendientes.get(0).getPartidoId());
        assertEquals("creador", pendientes.get(0).getRegistradoPorApodo());
        assertEquals("Club Norte", pendientes.get(0).getUbicacion());
        assertEquals(2, pendientes.get(0).getEquipoA().size());
        assertEquals(2, pendientes.get(0).getEquipoB().size());
    }

    @Test
    void listarResultadosGestionCreador_informaEstadosDeRegistroPendienteRechazadoYValidado() {
        PerfilJugador creador = perfil(1L, "creador", 101L);
        PerfilJugador jugador2 = perfil(2L, "jugador2", 102L);
        PerfilJugador jugador3 = perfil(3L, "jugador3", 103L);
        PerfilJugador jugador4 = perfil(4L, "jugador4", 104L);

        Partido registrable = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);
        registrable.setId(10L);
        registrable.setFechaHora(LocalDateTime.now().minusHours(1));

        Partido pendiente = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);
        pendiente.setId(20L);
        pendiente.setFechaHora(LocalDateTime.now().minusHours(2));

        Partido rechazado = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);
        rechazado.setId(30L);
        rechazado.setFechaHora(LocalDateTime.now().minusHours(3));

        Partido validado = partidoConCuatroJugadores(creador, jugador2, jugador3, jugador4);
        validado.setId(40L);
        validado.setFechaHora(LocalDateTime.now().minusHours(4));

        ResultadoPartido resultadoPendiente = resultadoExistente(
                pendiente,
                creador,
                EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION
        );
        ResultadoPartido resultadoRechazado = resultadoExistente(
                rechazado,
                creador,
                EstadoValidacionResultadoPartido.RECHAZADO
        );
        ResultadoPartido resultadoValidado = resultadoExistente(
                validado,
                creador,
                EstadoValidacionResultadoPartido.VALIDADO
        );

        when(perfilJugadorRepository.findByUsuarioId(101L)).thenReturn(Optional.of(creador));
        when(partidoRepository.findByCreadorIdWithJugadores(1L))
                .thenReturn(List.of(validado, registrable, rechazado, pendiente));
        when(resultadoPartidoRepository.findByCreadorIdWithDetalles(1L))
                .thenReturn(List.of(resultadoPendiente, resultadoRechazado, resultadoValidado));

        List<ResultadoPartidoGestionCreadorDto> resultados =
                resultadoPartidoService.listarResultadosGestionCreador(101L);

        assertTrue(resultados.stream()
                .anyMatch(dto -> dto.getPartidoId().equals(10L) && dto.isPuedeRegistrarResultado()));
        assertTrue(resultados.stream()
                .anyMatch(dto -> dto.getPartidoId().equals(20L) && dto.isResultadoPendienteValidacion()));
        assertTrue(resultados.stream()
                .anyMatch(dto -> dto.getPartidoId().equals(30L) && dto.isResultadoRechazado()));
        assertTrue(resultados.stream()
                .anyMatch(dto -> dto.getPartidoId().equals(40L) && dto.isResultadoValidado()));
    }

    private RegistrarResultadoPartidoRequestDto requestValido() {
        RegistrarResultadoPartidoRequestDto request = new RegistrarResultadoPartidoRequestDto();
        request.setEquipoAJugadorIds(List.of(1L, 2L));
        request.setEquipoBJugadorIds(List.of(3L, 4L));
        request.setTipoFinalizacion("FINALIZADO_NORMAL");
        request.setJuegosEquipoA(6);
        request.setJuegosEquipoB(4);
        return request;
    }

    private Partido partidoConCuatroJugadores(PerfilJugador creador,
                                              PerfilJugador jugador2,
                                              PerfilJugador jugador3,
                                              PerfilJugador jugador4) {
        Partido partido = new Partido();
        partido.setId(50L);
        partido.setCreador(creador);
        partido.setFechaHora(LocalDateTime.now().minusHours(2));
        partido.setCancelado(false);
        partido.setTerminado(false);
        partido.setJugadoresApuntados(new ArrayList<>(List.of(creador, jugador2, jugador3, jugador4)));
        return partido;
    }

    private ResultadoPartido resultadoExistente(Partido partido,
                                                PerfilJugador creador,
                                                EstadoValidacionResultadoPartido estado) {
        ResultadoPartido resultado = new ResultadoPartido();
        resultado.setId(200L);
        resultado.setPartido(partido);
        resultado.setRegistradoPor(creador);
        resultado.setEquipoAJugador1(partido.getJugadoresApuntados().get(0));
        resultado.setEquipoAJugador2(partido.getJugadoresApuntados().get(1));
        resultado.setEquipoBJugador1(partido.getJugadoresApuntados().get(2));
        resultado.setEquipoBJugador2(partido.getJugadoresApuntados().get(3));
        resultado.setTipoFinalizacion(TipoFinalizacionResultadoPartido.FINALIZADO_NORMAL);
        resultado.setJuegosEquipoA(6);
        resultado.setJuegosEquipoB(4);
        resultado.setEstadoValidacion(estado);
        resultado.setValidaciones(new ArrayList<>());
        resultado.setFechaRegistro(LocalDateTime.now().minusMinutes(10));
        return resultado;
    }

    private ValidacionResultadoPartido validacion(ResultadoPartido resultado,
                                                  PerfilJugador validador,
                                                  boolean aceptado) {
        ValidacionResultadoPartido validacion = new ValidacionResultadoPartido();
        validacion.setResultado(resultado);
        validacion.setValidador(validador);
        validacion.setAceptado(aceptado);
        validacion.setFechaValidacion(LocalDateTime.now().minusMinutes(5));
        return validacion;
    }

    private PerfilJugador perfil(Long perfilId, String apodo, Long usuarioId) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNombre(apodo);
        usuario.setEmail(apodo + "@mail.com");

        PerfilJugador perfil = new PerfilJugador();
        perfil.setId(perfilId);
        perfil.setApodo(apodo);
        perfil.setUsuario(usuario);
        perfil.setNivel(3.0);
        return perfil;
    }
}
