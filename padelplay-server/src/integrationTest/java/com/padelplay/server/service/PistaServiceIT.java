package com.padelplay.server.service;

import com.padelplay.server.entity.Pista;
import com.padelplay.server.entity.Reserva;
import com.padelplay.server.repository.PistaRepository;
import com.padelplay.server.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
class PistaServiceIT {

    @Autowired
    private PistaService pistaService;

    @Autowired
    private PistaRepository pistaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Test
    void buscarPistasDisponibles_noDebeDevolverPistaConReservaSolapada() {
        Pista pista = new Pista();
        pista.setNombre("Pista 1");
        pista.setZona("Centro");
        pista.setClub("Club Uno");
        pista = pistaRepository.save(pista);

        Reserva reserva = new Reserva();
        reserva.setPista(pista);
        reserva.setInicio(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0)));
        reserva.setFin(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(11, 30)));
        reservaRepository.save(reserva);

        List<Pista> resultado = pistaService.buscarPistasDisponibles(
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 30),
                LocalTime.of(11, 0),
                "Centro"
        );

        assertFalse(resultado.contains(pista));
        assertEquals(0, resultado.size());
    }
}
