package com.padelplay.cliente.controller;

import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PartidoDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/partidos")
public class PartidoWebController {

    private final RestTemplate restTemplate;
    private final PerfilServiceProxy perfilServiceProxy;

    private final String BACKEND_URL = "http://localhost:8080/api/partidos";

    public PartidoWebController(RestTemplate restTemplate, PerfilServiceProxy perfilServiceProxy) {
        this.restTemplate = restTemplate;
        this.perfilServiceProxy = perfilServiceProxy;
    }

    // =========================================================================
    // 1. DASHBOARD: EXPLORAR PARTIDOS (GET /partidos)
    // =========================================================================
    @GetMapping
    public String mostrarDashboard(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        Long perfilJugadorId = null;

        if (token != null) {
            try {
                EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
                model.addAttribute("estado", estado);
                if (estado != null && estado.getPerfilJugador() != null) {
                    perfilJugadorId = estado.getPerfilJugador().getId();
                    model.addAttribute("perfilJugadorId", perfilJugadorId);
                }
            } catch (Exception e) {
            }
        }

        try {
            ResponseEntity<PartidoDto[]> response = restTemplate.getForEntity(BACKEND_URL, PartidoDto[].class);
            List<PartidoDto> partidos = Arrays
                    .asList(response.getBody() != null ? response.getBody() : new PartidoDto[0]);
            model.addAttribute("partidos", partidos);

                if (perfilJugadorId != null) {
                Long finalPerfilJugadorId = perfilJugadorId;
                Set<Long> partidosApuntadoIds = partidos.stream()
                    .filter(p -> p.getJugadoresApuntados() != null && p.getJugadoresApuntados().stream()
                        .anyMatch(j -> finalPerfilJugadorId.equals(j.getId())))
                    .map(PartidoDto::getId)
                    .collect(Collectors.toSet());

                Set<Long> partidosCreadosIds = partidos.stream()
                    .filter(p -> p.getCreador() != null && finalPerfilJugadorId.equals(p.getCreador().getId()))
                    .map(PartidoDto::getId)
                    .collect(Collectors.toSet());

                Set<Long> partidosEliminablesIds = partidos.stream()
                    .filter(p -> p.getCreador() != null && finalPerfilJugadorId.equals(p.getCreador().getId()))
                    .filter(p -> p.getJugadoresApuntados() != null && p.getJugadoresApuntados().size() == 1)
                    .map(PartidoDto::getId)
                    .collect(Collectors.toSet());

                model.addAttribute("partidosApuntadoIds", partidosApuntadoIds);
                model.addAttribute("partidosCreadosIds", partidosCreadosIds);
                model.addAttribute("partidosEliminablesIds", partidosEliminablesIds);
                }
        } catch (Exception e) {
            model.addAttribute("error", "No se pudieron cargar los partidos. Inténtalo más tarde.");
            model.addAttribute("partidos", List.of());
        }

        return "partidos";
    }

    // =========================================================================
    // 2. MOSTRAR EL FORMULARIO DE CREACIÓN
    // =========================================================================
    @GetMapping("/crear")
    public String mostrarFormulario(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            model.addAttribute("estado", estado);
        } catch (Exception e) {
        }

        model.addAttribute("partido", new PartidoDto());
        return "crear-partido";
    }

    // =========================================================================
    // 3. RECIBIR EL FORMULARIO
    // =========================================================================
    @PostMapping("/crear")
    public String procesarFormulario(@ModelAttribute("partido") PartidoDto partidoDto,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) {
        try {
            String token = (String) session.getAttribute("token");
            if (token != null) {
                EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
                if (estado != null && estado.getPerfilJugador() != null) {
                    partidoDto.setCreador(estado.getPerfilJugador());
                }
            }

            ResponseEntity<PartidoDto> respuestaServidor = restTemplate.postForEntity(BACKEND_URL, partidoDto,
                    PartidoDto.class);
            redirectAttributes.addFlashAttribute("partido", respuestaServidor.getBody());

            return "redirect:/partidos/exito";

        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Error al crear el partido. Revisa los datos.");
            return "crear-partido";
        }
    }

    @GetMapping("/exito")
    public String mostrarExito(Model model, HttpSession session) {
        if (!model.containsAttribute("partido"))
            return "redirect:/partidos/crear";

        String token = (String) session.getAttribute("token");
        if (token != null) {
            try {
                EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
                model.addAttribute("estado", estado);
            } catch (Exception e) {
            }
        }
        return "exito";
    }

    // =========================================================================
    // 4. UNIRSE A UN PARTIDO (Desde el Pop-up)
    // =========================================================================
    @PostMapping("/{id}/unirse")
    public String unirseAPartido(@PathVariable("id") Long partidoId,
            @RequestParam(value = "codigoAcceso", required = false) String codigoAcceso, // NUEVO
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            // 1. Obtener el perfil del jugador activo
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            if (estado == null || estado.getPerfilJugador() == null) {
                redirectAttributes.addFlashAttribute("error", "Necesitas crear un perfil de jugador para unirte.");
                return "redirect:/partidos";
            }

            Long jugadorId = estado.getPerfilJugador().getId();

            // 2. Construir la URL y añadir el código si existe (NUEVO)
            String url = BACKEND_URL + "/" + partidoId + "/unirse?jugadorId=" + jugadorId;
            if (codigoAcceso != null && !codigoAcceso.trim().isEmpty()) {
                url += "&codigoAcceso=" + codigoAcceso.trim();
            }

            restTemplate.postForEntity(url, null, PartidoDto.class);

            // 3. Si todo va bien, mostramos mensaje de éxito
            redirectAttributes.addFlashAttribute("exito", "¡Te has unido al partido correctamente!");

        } catch (HttpClientErrorException e) {
            // 4. Si el backend lanza un error, lo mostramos
            String mensajeError = e.getResponseBodyAsString();
            redirectAttributes.addFlashAttribute("error", mensajeError != null && !mensajeError.isEmpty() ? mensajeError
                    : "No se ha podido procesar la solicitud.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al contactar con el servidor.");
        }

        // 5. Redirigimos siempre al Dashboard
        return "redirect:/partidos";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarAsistencia(@PathVariable("id") Long partidoId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            if (estado == null || estado.getPerfilJugador() == null) {
                redirectAttributes.addFlashAttribute("error", "Necesitas perfil de jugador para cancelar asistencia.");
                return "redirect:/partidos";
            }

            Long jugadorId = estado.getPerfilJugador().getId();
            String url = BACKEND_URL + "/" + partidoId + "/cancelar?usuarioId=" + jugadorId;
            restTemplate.postForEntity(url, null, PartidoDto.class);
            redirectAttributes.addFlashAttribute("exito", "Tu asistencia se ha cancelado correctamente.");
        } catch (HttpClientErrorException e) {
            String mensajeError = e.getResponseBodyAsString();
            redirectAttributes.addFlashAttribute("error", mensajeError != null && !mensajeError.isEmpty() ? mensajeError
                    : "No se ha podido procesar la cancelación.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al contactar con el servidor.");
        }

        return "redirect:/partidos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarPartidoSiSolo(@PathVariable("id") Long partidoId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            if (estado == null || estado.getPerfilJugador() == null) {
                redirectAttributes.addFlashAttribute("error", "Necesitas perfil de jugador para eliminar el partido.");
                return "redirect:/partidos";
            }

            Long jugadorId = estado.getPerfilJugador().getId();
            String url = BACKEND_URL + "/" + partidoId + "/eliminar?usuarioId=" + jugadorId;
            restTemplate.postForEntity(url, null, Void.class);
            redirectAttributes.addFlashAttribute("exito", "Partido eliminado correctamente.");
        } catch (HttpClientErrorException e) {
            String mensajeError = e.getResponseBodyAsString();
            redirectAttributes.addFlashAttribute("error", mensajeError != null && !mensajeError.isEmpty() ? mensajeError
                    : "No se ha podido eliminar el partido.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al contactar con el servidor.");
        }

        return "redirect:/partidos";
    }
}