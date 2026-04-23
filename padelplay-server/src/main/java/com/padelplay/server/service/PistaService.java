package com.padelplay.server.service;

import com.padelplay.server.entity.Pista;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PistaRepository;
import com.padelplay.server.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class PistaService {

    private static final long DURACION_PARTIDO_MINUTOS = 90;

    private final PistaRepository pistaRepository;
    private final ReservaRepository reservaRepository;
    private final PartidoRepository partidoRepository;

    public PistaService(PistaRepository pistaRepository,
                        ReservaRepository reservaRepository,
                        PartidoRepository partidoRepository) {
        this.pistaRepository = pistaRepository;
        this.reservaRepository = reservaRepository;
        this.partidoRepository = partidoRepository;
    }

    @Transactional(readOnly = true)
    public List<Pista> buscarPistasDisponibles(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String zona) {
        validarParametrosBusqueda(fecha, horaInicio, horaFin, zona);

        LocalDateTime inicioFranja = LocalDateTime.of(fecha, horaInicio);
        LocalDateTime finFranja = LocalDateTime.of(fecha, horaFin);

        return pistaRepository.findByZonaIgnoreCase(zona).stream()
                .filter(pista -> !estaReservada(pista.getId(), inicioFranja, finFranja))
                .filter(pista -> !tienePartidoEnFranja(pista.getNombre(), inicioFranja, finFranja))
                .toList();
    }

    private boolean estaReservada(Long pistaId, LocalDateTime inicioFranja, LocalDateTime finFranja) {
        return reservaRepository.existsByPistaIdAndInicioLessThanAndFinGreaterThan(pistaId, finFranja, inicioFranja);
    }

    private boolean tienePartidoEnFranja(String nombrePista, LocalDateTime inicioFranja, LocalDateTime finFranja) {
        LocalDateTime umbralInicio = inicioFranja.minusMinutes(DURACION_PARTIDO_MINUTOS);
        return partidoRepository.existsByUbicacionIgnoreCaseAndCanceladoFalseAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                nombrePista,
                umbralInicio,
                finFranja
        );
    }

    private void validarParametrosBusqueda(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String zona) {
        if (fecha == null || horaInicio == null || horaFin == null || zona == null || zona.isBlank()) {
            throw new IllegalArgumentException("La fecha, franja horaria y zona son obligatorias.");
        }

        LocalDateTime inicioSolicitado = LocalDateTime.of(fecha, horaInicio);
        if (inicioSolicitado.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora de búsqueda no pueden estar en el pasado.");
        }

        if (!horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora fin.");
        }
    }
}
