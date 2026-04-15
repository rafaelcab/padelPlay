package com.padelplay.server.facade;

import com.padelplay.common.dto.PistaDto;
import com.padelplay.server.entity.Pista;
import com.padelplay.server.service.PistaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/pistas")
public class PistaController {

    private final PistaService pistaService;

    public PistaController(PistaService pistaService) {
        this.pistaService = pistaService;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<?> buscarPistasDisponibles(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("horaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam("horaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            @RequestParam("zona") String zona) {
        try {
            List<Pista> pistas = pistaService.buscarPistasDisponibles(fecha, horaInicio, horaFin, zona);
            List<PistaDto> resultado = pistas.stream().map(this::toDto).toList();
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private PistaDto toDto(Pista pista) {
        PistaDto dto = new PistaDto();
        dto.setId(pista.getId());
        dto.setNombre(pista.getNombre());
        dto.setZona(pista.getZona());
        dto.setClub(pista.getClub());
        return dto;
    }
}
