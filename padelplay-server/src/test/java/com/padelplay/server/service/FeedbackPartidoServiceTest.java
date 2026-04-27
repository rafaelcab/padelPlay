package com.padelplay.server.service;

import com.padelplay.server.entity.*;
import com.padelplay.server.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackPartidoServiceTest {

    @Mock
    private FeedbackPartidoRepository feedbackPartidoRepository;
    @Mock
    private PerfilEntrenadorRepository perfilEntrenadorRepository;
    @Mock
    private PartidoRepository partidoRepository;
    @Mock
    private PerfilJugadorRepository perfilJugadorRepository;

    @InjectMocks
    private FeedbackPartidoService feedbackPartidoService;

    @Test
    void guardarFeedback_debeFallarSiAlumnoNoParticipo() {
        Long entrenadorId = 1L;
        Long alumnoId = 2L;
        Long partidoId = 3L;

        PerfilEntrenador entrenador = new PerfilEntrenador();
        PerfilJugador alumno = new PerfilJugador();
        alumno.setId(alumnoId);
        Partido partido = new Partido();
        partido.setId(partidoId);
        partido.setCreador(new PerfilJugador()); // Creador diferente
        partido.setJugadoresApuntados(new ArrayList<>()); // Lista vacía

        when(perfilEntrenadorRepository.findById(entrenadorId)).thenReturn(Optional.of(entrenador));
        when(perfilJugadorRepository.findById(alumnoId)).thenReturn(Optional.of(alumno));
        when(partidoRepository.findById(partidoId)).thenReturn(Optional.of(partido));

        RuntimeException ex = assertThrows(RuntimeException.class, 
                () -> feedbackPartidoService.guardarFeedback(entrenadorId, alumnoId, partidoId, 5.0, "Bien", "Saque", "Volea"));

        assertTrue(ex.getMessage().contains("no participó"));
    }

    @Test
    void guardarFeedback_debeGuardarCorrectamente() {
        Long entrenadorId = 1L;
        Long alumnoId = 2L;
        Long partidoId = 3L;

        PerfilEntrenador entrenador = new PerfilEntrenador();
        entrenador.setId(entrenadorId);
        PerfilJugador alumno = new PerfilJugador();
        alumno.setId(alumnoId);
        Partido partido = new Partido();
        partido.setId(partidoId);
        partido.setJugadoresApuntados(new ArrayList<>(List.of(alumno)));

        when(perfilEntrenadorRepository.findById(entrenadorId)).thenReturn(Optional.of(entrenador));
        when(perfilJugadorRepository.findById(alumnoId)).thenReturn(Optional.of(alumno));
        when(partidoRepository.findById(partidoId)).thenReturn(Optional.of(partido));
        when(feedbackPartidoRepository.findByEntrenadorIdAndPartidoId(entrenadorId, partidoId)).thenReturn(Optional.empty());
        when(feedbackPartidoRepository.save(any(FeedbackPartido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        feedbackPartidoService.guardarFeedback(entrenadorId, alumnoId, partidoId, 4.0, "OK", "F1", "A1");

        verify(feedbackPartidoRepository).save(any(FeedbackPartido.class));
    }
}
