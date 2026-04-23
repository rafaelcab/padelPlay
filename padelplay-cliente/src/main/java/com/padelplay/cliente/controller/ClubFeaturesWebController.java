package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.OcupacionClubDto;
import com.padelplay.common.dto.PistaDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping
public class ClubFeaturesWebController {

    private final RestTemplate restTemplate;
    private final PerfilServiceProxy perfilServiceProxy;

    public ClubFeaturesWebController(RestTemplate restTemplate, PerfilServiceProxy perfilServiceProxy) {
        this.restTemplate = restTemplate;
        this.perfilServiceProxy = perfilServiceProxy;
    }

    @GetMapping("/pistas/buscar")
    public String buscarPistas(
            @RequestParam(value = "fecha", required = false) LocalDate fecha,
            @RequestParam(value = "horaInicio", required = false) LocalTime horaInicio,
            @RequestParam(value = "horaFin", required = false) LocalTime horaFin,
            @RequestParam(value = "zona", required = false) String zona,
            Model model,
            HttpSession session) {
        cargarEstadoPerfil(model, session);

        model.addAttribute("fecha", fecha);
        model.addAttribute("horaInicio", horaInicio);
        model.addAttribute("horaFin", horaFin);
        model.addAttribute("zona", zona);

        if (fecha == null || horaInicio == null || horaFin == null || zona == null || zona.isBlank()) {
            model.addAttribute("pistas", List.of());
            return "pistas-buscar";
        }

        try {
            String url = UriComponentsBuilder
                    .fromUriString("http://localhost:8080/api/pistas/disponibles")
                    .queryParam("fecha", fecha)
                    .queryParam("horaInicio", horaInicio)
                    .queryParam("horaFin", horaFin)
                    .queryParam("zona", zona)
                    .toUriString();

            ResponseEntity<List<PistaDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            model.addAttribute("pistas", response.getBody() != null ? response.getBody() : List.of());
        } catch (Exception e) {
            model.addAttribute("error", "No se pudieron consultar las pistas disponibles.");
            model.addAttribute("pistas", List.of());
        }

        return "pistas-buscar";
    }

    @GetMapping("/estadisticas")
    public String estadisticas(
            @RequestParam(value = "fecha", required = false) LocalDate fecha,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        cargarEstadoPerfil(model, session);

        if (!esAdminClub(model)) {
            redirectAttributes.addFlashAttribute("error", "Solo los administradores de club pueden ver estadísticas.");
            return "redirect:/partidos";
        }

        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        model.addAttribute("fecha", fechaConsulta);

        try {
            String url = UriComponentsBuilder
                    .fromUriString("http://localhost:8080/api/estadisticas/ocupacion-por-hora")
                    .queryParam("fecha", fechaConsulta)
                    .toUriString();

            ResponseEntity<List<OcupacionClubDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            model.addAttribute("ocupaciones", response.getBody() != null ? response.getBody() : List.of());
        } catch (Exception e) {
            model.addAttribute("error", "No se pudieron cargar las estadísticas.");
            model.addAttribute("ocupaciones", List.of());
        }

        return "estadisticas";
    }

    private void cargarEstadoPerfil(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return;
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            model.addAttribute("estado", estado);
        } catch (Exception ignored) {
        }
    }

    private boolean esAdminClub(Model model) {
        Object valor = model.getAttribute("esAdminClub");
        return valor instanceof Boolean && (Boolean) valor;
    }
}
