package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.EvaluacionServiceProxy;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.common.dto.CompletarEvaluacionDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.ResponderSolicitudEvaluacionDto;
import com.padelplay.common.dto.SolicitudEvaluacionDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/entrenador/evaluaciones")
public class EntrenadorEvaluacionViewController {

    private final EvaluacionServiceProxy evaluacionServiceProxy;
    private final PerfilServiceProxy perfilServiceProxy;

    public EntrenadorEvaluacionViewController(EvaluacionServiceProxy evaluacionServiceProxy,
                                              PerfilServiceProxy perfilServiceProxy) {
        this.evaluacionServiceProxy = evaluacionServiceProxy;
        this.perfilServiceProxy = perfilServiceProxy;
    }

    @GetMapping
    public String listarEvaluaciones(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            if (estado.isRequiereSeleccionPerfil()) {
                return "redirect:/perfil/seleccionar-rol";
            }

            List<SolicitudEvaluacionDto> solicitudes = cargarSolicitudesRecibidas(token);
            model.addAttribute("estado", estado);
            model.addAttribute("evaluacionesProgramadas", filtrarPorEstado(solicitudes, "ACEPTADA"));
            model.addAttribute("solicitudesGestion", excluirEstado(solicitudes, "ACEPTADA"));

            if (!estado.isTienePerfilEntrenador()) {
                model.addAttribute("error", "Necesitas un perfil de entrenador para gestionar evaluaciones.");
            }

            return "evaluaciones-entrenador";
        } catch (Exception e) {
            if (esSesionInvalida(e)) {
                return redirigirALogin(session);
            }
            model.addAttribute("error", "Error al cargar evaluaciones: " + e.getMessage());
            model.addAttribute("evaluacionesProgramadas", List.of());
            model.addAttribute("solicitudesGestion", List.of());
            return "evaluaciones-entrenador";
        }
    }

    @PostMapping("/{id}/aceptar")
    public String aceptarSolicitud(@PathVariable Long id,
                                   @RequestParam(required = false) String comentarioEntrenador,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            ResponderSolicitudEvaluacionDto request = new ResponderSolicitudEvaluacionDto();
            request.setComentarioEntrenador(comentarioEntrenador);
            evaluacionServiceProxy.aceptarSolicitud(token, id, request);
            redirectAttributes.addFlashAttribute("exito", "Solicitud aceptada correctamente.");
        } catch (Exception e) {
            if (esSesionInvalida(e)) {
                return redirigirALogin(session);
            }
            redirectAttributes.addFlashAttribute("error", "No se pudo aceptar la solicitud: " + e.getMessage());
        }

        return "redirect:/entrenador/evaluaciones";
    }

    @PostMapping("/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable Long id,
                                    @RequestParam(required = false) String comentarioEntrenador,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            ResponderSolicitudEvaluacionDto request = new ResponderSolicitudEvaluacionDto();
            request.setComentarioEntrenador(comentarioEntrenador);
            evaluacionServiceProxy.rechazarSolicitud(token, id, request);
            redirectAttributes.addFlashAttribute("exito", "Solicitud rechazada correctamente.");
        } catch (Exception e) {
            if (esSesionInvalida(e)) {
                return redirigirALogin(session);
            }
            redirectAttributes.addFlashAttribute("error", "No se pudo rechazar la solicitud: " + e.getMessage());
        }

        return "redirect:/entrenador/evaluaciones";
    }

    @GetMapping("/{id}/completar")
    public String mostrarCompletarEvaluacion(@PathVariable Long id,
                                             HttpSession session,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            SolicitudEvaluacionDto solicitud = buscarSolicitudRecibida(token, id);

            if (solicitud == null) {
                redirectAttributes.addFlashAttribute("error", "Solicitud de evaluacion no encontrada.");
                return "redirect:/entrenador/evaluaciones";
            }
            if (!"ACEPTADA".equals(solicitud.getEstado())) {
                redirectAttributes.addFlashAttribute("error", "Solo se pueden completar solicitudes aceptadas.");
                return "redirect:/entrenador/evaluaciones";
            }

            model.addAttribute("estado", estado);
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("completarEvaluacion", new CompletarEvaluacionDto());
            return "completar-evaluacion";
        } catch (Exception e) {
            if (esSesionInvalida(e)) {
                return redirigirALogin(session);
            }
            redirectAttributes.addFlashAttribute("error", "Error al cargar la evaluacion: " + e.getMessage());
            return "redirect:/entrenador/evaluaciones";
        }
    }

    @PostMapping("/{id}/completar")
    public String completarEvaluacion(@PathVariable Long id,
                                      @RequestParam Integer nuevoElo,
                                      @RequestParam(required = false) String observaciones,
                                      @RequestParam(required = false) Integer control,
                                      @RequestParam(required = false) Integer potencia,
                                      @RequestParam(required = false) Integer consistencia,
                                      @RequestParam(required = false) Integer posicionamiento,
                                      @RequestParam(required = false) Integer tactica,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            CompletarEvaluacionDto request = new CompletarEvaluacionDto();
            request.setSolicitudId(id);
            request.setNuevoElo(nuevoElo);
            request.setObservaciones(observaciones);
            request.setControl(control);
            request.setPotencia(potencia);
            request.setConsistencia(consistencia);
            request.setPosicionamiento(posicionamiento);
            request.setTactica(tactica);

            evaluacionServiceProxy.completarEvaluacion(token, id, request);
            redirectAttributes.addFlashAttribute("exito", "Evaluacion completada y ELO actualizado.");
            return "redirect:/entrenador/evaluaciones";
        } catch (Exception e) {
            if (esSesionInvalida(e)) {
                return redirigirALogin(session);
            }
            redirectAttributes.addFlashAttribute("error", "No se pudo completar la evaluacion: " + e.getMessage());
            return "redirect:/entrenador/evaluaciones/" + id + "/completar";
        }
    }

    private List<SolicitudEvaluacionDto> cargarSolicitudesRecibidas(String token) {
        List<SolicitudEvaluacionDto> solicitudes = evaluacionServiceProxy.obtenerSolicitudesRecibidas(token);
        return solicitudes != null ? solicitudes : List.of();
    }

    private SolicitudEvaluacionDto buscarSolicitudRecibida(String token, Long solicitudId) {
        return cargarSolicitudesRecibidas(token).stream()
                .filter(solicitud -> solicitudId.equals(solicitud.getId()))
                .findFirst()
                .orElse(null);
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
