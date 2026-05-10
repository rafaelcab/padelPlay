package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.EvaluacionServiceProxy;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.common.dto.CrearSolicitudEvaluacionDto;
import com.padelplay.common.dto.EntrenadorDisponibleDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.SolicitudEvaluacionDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/perfil/evaluacion")
public class EvaluacionViewController {

    private final EvaluacionServiceProxy evaluacionServiceProxy;
    private final PerfilServiceProxy perfilServiceProxy;

    public EvaluacionViewController(EvaluacionServiceProxy evaluacionServiceProxy,
                                    PerfilServiceProxy perfilServiceProxy) {
        this.evaluacionServiceProxy = evaluacionServiceProxy;
        this.perfilServiceProxy = perfilServiceProxy;
    }

    @GetMapping
    public String mostrarEvaluacionJugador(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            if (estado.isRequiereSeleccionPerfil()) {
                return "redirect:/perfil/seleccionar-rol";
            }

            List<SolicitudEvaluacionDto> solicitudes = cargarSolicitudes(token);
            model.addAttribute("estado", estado);
            model.addAttribute("entrenadores", cargarEntrenadores(token));
            model.addAttribute("proximasEvaluaciones", filtrarPorEstado(solicitudes, "ACEPTADA"));
            model.addAttribute("solicitudesHistorial", excluirEstado(solicitudes, "ACEPTADA"));

            if (!estado.isTienePerfilJugador()) {
                model.addAttribute("error", "Necesitas un perfil de jugador para solicitar una evaluacion.");
            }

            return "evaluacion-jugador";
        } catch (Exception e) {
            if (esSesionInvalida(e)) {
                return redirigirALogin(session);
            }
            model.addAttribute("error", "Error al cargar evaluaciones: " + e.getMessage());
            model.addAttribute("entrenadores", List.of());
            model.addAttribute("proximasEvaluaciones", List.of());
            model.addAttribute("solicitudesHistorial", List.of());
            return "evaluacion-jugador";
        }
    }

    @PostMapping("/solicitar")
    public String solicitarEvaluacion(@RequestParam Long entrenadorId,
                                      @RequestParam
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                      LocalDateTime fechaHoraSolicitada,
                                      @RequestParam(required = false) String comentarioJugador,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            CrearSolicitudEvaluacionDto request = new CrearSolicitudEvaluacionDto();
            request.setEntrenadorId(entrenadorId);
            request.setFechaHoraSolicitada(fechaHoraSolicitada);
            request.setComentarioJugador(comentarioJugador);

            evaluacionServiceProxy.crearSolicitudEvaluacion(token, request);
            redirectAttributes.addFlashAttribute("exito", "Solicitud de evaluacion enviada correctamente.");
        } catch (Exception e) {
            if (esSesionInvalida(e)) {
                return redirigirALogin(session);
            }
            redirectAttributes.addFlashAttribute("error", "No se pudo enviar la solicitud: " + e.getMessage());
        }

        return "redirect:/perfil/evaluacion";
    }

    private List<EntrenadorDisponibleDto> cargarEntrenadores(String token) {
        List<EntrenadorDisponibleDto> entrenadores = evaluacionServiceProxy.obtenerEntrenadoresDisponibles(token);
        return entrenadores != null ? entrenadores : List.of();
    }

    private List<SolicitudEvaluacionDto> cargarSolicitudes(String token) {
        List<SolicitudEvaluacionDto> solicitudes = evaluacionServiceProxy.obtenerMisSolicitudes(token);
        return solicitudes != null ? solicitudes : List.of();
    }

    private List<SolicitudEvaluacionDto> filtrarPorEstado(List<SolicitudEvaluacionDto> solicitudes, String estado) {
        return solicitudes.stream()
                .filter(solicitud -> estado.equals(solicitud.getEstado()))
                .toList();
    }

    private List<SolicitudEvaluacionDto> excluirEstado(List<SolicitudEvaluacionDto> solicitudes, String estado) {
        return solicitudes.stream()
                .filter(solicitud -> !estado.equals(solicitud.getEstado()))
                .toList();
    }

    private boolean esSesionInvalida(Exception e) {
        return e instanceof HttpClientErrorException.Unauthorized
                || e instanceof HttpClientErrorException.Forbidden;
    }

    private String redirigirALogin(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
