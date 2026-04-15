package com.padelplay.server.service;

import com.padelplay.common.dto.OcupacionClubDto;
import com.padelplay.server.entity.Pista;
import com.padelplay.server.repository.PartidoRepository;
import com.padelplay.server.repository.PistaRepository;
import com.padelplay.server.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    private static final int HORA_INICIO = 8;
    private static final int HORA_FIN = 22;
    private static final int DURACION_PARTIDO_MINUTOS = 90;

    private final PistaRepository pistaRepository;
    private final ReservaRepository reservaRepository;
    private final PartidoRepository partidoRepository;

    public EstadisticasService(PistaRepository pistaRepository,
                               ReservaRepository reservaRepository,
                               PartidoRepository partidoRepository) {
        this.pistaRepository = pistaRepository;
        this.reservaRepository = reservaRepository;
        this.partidoRepository = partidoRepository;
    }

    @Transactional(readOnly = true)
    public List<OcupacionClubDto> calcularOcupacionPorHora(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }

        List<Pista> pistas = pistaRepository.findAll();
        long totalPistas = pistas.size();

        if (totalPistas == 0L) {
            return calcularOcupacionConPartidosSinPistas(fecha);
        }

        Map<String, Long> pistaIdsPorNombre = pistas.stream()
                .collect(Collectors.toMap(
                        pista -> normalizar(pista.getNombre()),
                        Pista::getId,
                        (primero, duplicado) -> primero
                ));

        List<OcupacionClubDto> resultados = new ArrayList<>();

        for (int hora = HORA_INICIO; hora <= HORA_FIN; hora++) {
            LocalDateTime inicio = fecha.atTime(hora, 0);
            LocalDateTime fin = inicio.plusHours(1);

            Set<Long> ocupadas = new HashSet<>(reservaRepository.findDistinctPistaIdsOcupadasEnFranja(inicio, fin));
            List<String> pistasOcupadasPorPartido = partidoRepository.findDistinctUbicacionesOcupadasEnFranja(
                    inicio.minusMinutes(DURACION_PARTIDO_MINUTOS),
                    fin
            );

            for (String nombrePista : pistasOcupadasPorPartido) {
                Long pistaId = pistaIdsPorNombre.get(normalizar(nombrePista));
                if (pistaId != null) {
                    ocupadas.add(pistaId);
                }
            }

            OcupacionClubDto dto = new OcupacionClubDto();
            dto.setHora(String.format("%02d:00", hora));
            dto.setPorcentajeOcupacion((ocupadas.size() * 100.0) / totalPistas);
            resultados.add(dto);
        }

        return resultados;
    }

    private List<OcupacionClubDto> calcularOcupacionConPartidosSinPistas(LocalDate fecha) {
        Map<Integer, Set<String>> ocupadasPorHora = new HashMap<>();
        Set<String> ubicacionesTotales = new HashSet<>();

        for (int hora = HORA_INICIO; hora <= HORA_FIN; hora++) {
            LocalDateTime inicio = fecha.atTime(hora, 0);
            LocalDateTime fin = inicio.plusHours(1);

            Set<String> ocupadasEnFranja = partidoRepository
                    .findDistinctUbicacionesOcupadasEnFranja(inicio.minusMinutes(DURACION_PARTIDO_MINUTOS), fin)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(this::normalizar)
                    .filter(valor -> !valor.isBlank())
                    .collect(Collectors.toSet());

            ocupadasPorHora.put(hora, ocupadasEnFranja);
            ubicacionesTotales.addAll(ocupadasEnFranja);
        }

        if (ubicacionesTotales.isEmpty()) {
            return generarHorasConCero();
        }

        double totalUbicaciones = ubicacionesTotales.size();
        List<OcupacionClubDto> resultados = new ArrayList<>();

        for (int hora = HORA_INICIO; hora <= HORA_FIN; hora++) {
            OcupacionClubDto dto = new OcupacionClubDto();
            dto.setHora(String.format("%02d:00", hora));
            dto.setPorcentajeOcupacion((ocupadasPorHora.getOrDefault(hora, Set.of()).size() * 100.0) / totalUbicaciones);
            resultados.add(dto);
        }

        return resultados;
    }

    private List<OcupacionClubDto> generarHorasConCero() {
        List<OcupacionClubDto> resultados = new ArrayList<>();
        for (int hora = HORA_INICIO; hora <= HORA_FIN; hora++) {
            OcupacionClubDto dto = new OcupacionClubDto();
            dto.setHora(String.format("%02d:00", hora));
            dto.setPorcentajeOcupacion(0.0);
            resultados.add(dto);
        }
        return resultados;
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase();
    }
}
