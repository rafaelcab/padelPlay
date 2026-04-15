package com.padelplay.server.facade;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.padelplay.common.dto.PartidoDto;
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
    // 1. OBTENER TODOS LOS PARTIDOS (DASHBOARD)
    // GET /api/partidos
    // =========================================================================
    @Operation(summary = "Listar todos los partidos", description = "Recupera la lista completa de partidos para mostrar en el Dashboard.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de partidos recuperada con éxito")
    })
    @GetMapping
    public ResponseEntity<List<PartidoDto>> obtenerTodosLosPartidos() {
        List<PartidoDto> partidos = partidoService.listarPartidos();
        return new ResponseEntity<>(partidos, HttpStatus.OK);
    }

    // =========================================================================
    // 2. CREAR PARTIDO
    // POST /api/partidos
    // =========================================================================
    @Operation(summary = "Crear un nuevo partido", description = "Crea un partido, vincula al perfil del creador y establece los huecos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Partido creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (nivel fuera de rango, fecha pasada, etc.)")
    })
    @PostMapping
    public ResponseEntity<PartidoDto> crearPartido(@RequestBody PartidoDto partidoDto) {
        try {
            PartidoDto nuevoPartidoDto = partidoService.crearPartido(partidoDto);
            return new ResponseEntity<>(nuevoPartidoDto, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================================================
    // 3. UNIRSE A UN PARTIDO
    // POST /api/partidos/{id}/unirse?jugadorId={id}&codigoAcceso={codigo}
    // =========================================================================
    @Operation(summary = "Unirse a un partido", description = "Añade un jugador a la lista del partido y reduce los huecos disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jugador añadido correctamente al partido"),
            @ApiResponse(responseCode = "400", description = "El partido está lleno, el jugador ya está apuntado o los datos son incorrectos")
    })
    @PostMapping("/{id}/unirse")
    public ResponseEntity<?> unirseAPartido(
            @PathVariable("id") Long partidoId,
            @RequestParam("jugadorId") Long jugadorId,
            @RequestParam(value = "codigoAcceso", required = false) String codigoAcceso) {
        try {
            PartidoDto partidoActualizado = partidoService.unirseAPartido(partidoId, jugadorId, codigoAcceso);
            return ResponseEntity.ok(partidoActualizado);

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Devolvemos el mensaje de la excepción para que el cliente (8081) pueda
            // mostrarlo en un alert o popup
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarAsistencia(
            @PathVariable("id") Long partidoId,
            @RequestParam("usuarioId") Long usuarioId) {
        try {
            PartidoDto partidoActualizado = partidoService.cancelarAsistencia(partidoId, usuarioId);
            return ResponseEntity.ok(partidoActualizado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/eliminar")
    public ResponseEntity<?> eliminarPartidoSiSolo(
            @PathVariable("id") Long partidoId,
            @RequestParam("usuarioId") Long usuarioId) {
        try {
            partidoService.eliminarPartidoSiSolo(partidoId, usuarioId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}