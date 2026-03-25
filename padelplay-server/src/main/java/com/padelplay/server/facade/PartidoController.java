package com.padelplay.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.padelplay.common.dto.PartidoDto;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.service.PartidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/partidos")
@Tag(name = "Partido Controller", description = "Operaciones sobre los partidos de la red de pádel")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    // =========================================================================
    // 1. CREAR PARTIDO 
    // POST /api/partidos
    // =========================================================================
    @Operation(
        summary = "Crear un nuevo partido ",
        description = "Crea un partido, vincula al jugador creador y establece automáticamente los huecos disponibles a 3."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partido creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (nivel fuera de rango, fecha en el pasado, o jugador no encontrado)")
    })
    @PostMapping
    public ResponseEntity<Partido> crearPartido(@RequestBody PartidoDto partidoDto) {
        try {
            // Lógica de dominio en el servicio
            Partido nuevoPartido = partidoService.crearPartido(partidoDto);
            
            return new ResponseEntity<>(nuevoPartido, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Capturamos las excepciones de validación que lanzamos en el PartidoService
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}