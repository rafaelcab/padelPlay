package com.padelplay.server.service;

import com.padelplay.common.dto.PartidoDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.server.entity.AuthProvider;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PerfilJugadorRepository;
import com.padelplay.server.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
class PartidoServiceIT {

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilJugadorRepository perfilJugadorRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Test
    void crearPartidoYUnirse_debePersistirYActualizarHuecos() {
        PerfilJugador creador = crearPerfil("creador@padelplay.com", "Creador", "creador");
        PerfilJugador invitado = crearPerfil("invitado@padelplay.com", "Invitado", "invitado");

        PartidoDto nuevo = new PartidoDto();
        nuevo.setCreador(toPerfilDto(creador.getId()));
        nuevo.setFechaHora(LocalDateTime.now().plusDays(2));
        nuevo.setUbicacion("Bilbao");
        nuevo.setTipoPartido("private");
        nuevo.setNivelRequerido(3.5);

        PartidoDto creado = partidoService.crearPartido(nuevo);
        assertNotNull(creado.getId());
        assertEquals("PRIVADO", creado.getTipoPartido());
        assertEquals(3, creado.getHuecosDisponibles());
        assertNotNull(creado.getCodigoAcceso());

        PartidoDto actualizado = partidoService.unirseAPartido(creado.getId(), invitado.getId(), creado.getCodigoAcceso());
        assertEquals(2, actualizado.getHuecosDisponibles());
        assertEquals(2, actualizado.getJugadoresApuntados().size());

        Partido persisted = partidoRepository.findById(creado.getId()).orElseThrow();
        assertEquals(2, persisted.getJugadoresApuntados().size());
        assertEquals(2, persisted.getHuecosDisponibles());
    }

    @Test
    void cancelarAsistencia_debeEliminarJugadorYDejarPartidoActivo() {
        PerfilJugador creador = crearPerfil("creador2@padelplay.com", "Creador2", "creador2");
        PerfilJugador invitado = crearPerfil("invitado2@padelplay.com", "Invitado2", "invitado2");

        PartidoDto nuevo = new PartidoDto();
        nuevo.setCreador(toPerfilDto(creador.getId()));
        nuevo.setFechaHora(LocalDateTime.now().plusDays(2));
        nuevo.setUbicacion("Bilbao");
        nuevo.setTipoPartido("public");
        nuevo.setNivelRequerido(3.5);

        PartidoDto creado = partidoService.crearPartido(nuevo);
        partidoService.unirseAPartido(creado.getId(), invitado.getId(), null);

        PartidoDto cancelado = partidoService.cancelarAsistencia(creado.getId(), invitado.getId());

        Partido persisted = partidoRepository.findById(creado.getId()).orElseThrow();
        assertFalse(persisted.isCancelado());
        assertEquals(1, persisted.getJugadoresApuntados().size());
        assertEquals(3, persisted.getHuecosDisponibles());
        assertEquals(1, cancelado.getJugadoresApuntados().size());
        assertFalse(cancelado.isCancelado());
    }

    private PerfilJugador crearPerfil(String email, String nombre, String apodo) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setNombre(nombre);
        usuario.setPassword("123456");
        usuario.setAuthProvider(AuthProvider.LOCAL);
        usuario = usuarioRepository.save(usuario);

        PerfilJugador perfil = new PerfilJugador();
        perfil.setUsuario(usuario);
        perfil.setApodo(apodo);
        perfil.setNivel(3.0);
        return perfilJugadorRepository.save(perfil);
    }

    private PerfilJugadorDto toPerfilDto(Long id) {
        PerfilJugadorDto dto = new PerfilJugadorDto();
        dto.setId(id);
        return dto;
    }
}
