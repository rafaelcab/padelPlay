package com.padelplay.cliente.controller;

import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PartidoPendienteReporteDto;
import com.padelplay.common.dto.ParticipantePendienteReporteDto;
import com.padelplay.common.dto.ReporteExperienciaRequestDto;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.cliente.proxies.ReporteExperienciaProxy;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/perfil/reportes")
public class ReporteExperienciaWebController {

    private final PerfilServiceProxy perfilServiceProxy;
    private final ReporteExperienciaProxy reporteExperienciaProxy;

    public ReporteExperienciaWebController(PerfilServiceProxy perfilServiceProxy,
                                           ReporteExperienciaProxy reporteExperienciaProxy) {
        this.perfilServiceProxy = perfilServiceProxy;
        this.reporteExperienciaProxy = reporteExperienciaProxy;
    }

    @GetMapping("/{partidoId}")
    public String mostrarFormularioReporte(@PathVariable Long partidoId, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            model.addAttribute("estado", estado);

            if (estado == null || estado.getPerfilJugador() == null) {
                return "redirect:/perfil/dashboard?error=Necesitas perfil de jugador para reportar experiencias.";
            }

            Optional<PartidoPendienteReporteDto> partido = obtenerPartido(token, partidoId);
            if (partido.isEmpty()) {
                return "redirect:/perfil/dashboard?error=No puedes reportar este partido.";
            }

            List<ParticipantePendienteReporteDto> participantesPendientes =
                    reporteExperienciaProxy.obtenerParticipantesPendientes(token, partidoId);
            List<String> motivosDisponibles = reporteExperienciaProxy.obtenerMotivosReporte();

            model.addAttribute("partido", partido.get());
            model.addAttribute("participantesPendientes",
                    participantesPendientes != null ? participantesPendientes : List.of());
            model.addAttribute("motivosDisponibles", motivosDisponibles != null ? motivosDisponibles : List.of());
            return "reporte-experiencia";
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            session.invalidate();
            return "redirect:/login";
        } catch (HttpClientErrorException.BadRequest e) {
            return "redirect:/perfil/dashboard?error=No se puede abrir el reporte para ese partido.";
        } catch (Exception e) {
            return "redirect:/perfil/dashboard?error=No se pudo cargar el formulario de reporte.";
        }
    }

    @PostMapping("/{partidoId}")
    public String crearReporte(@PathVariable Long partidoId,
                               @RequestParam Long reportadoId,
                               @RequestParam Integer valoracion,
                               @RequestParam(required = false) String comentario,
                               @RequestParam(required = false) String[] motivos,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            ReporteExperienciaRequestDto request = new ReporteExperienciaRequestDto();
            request.setReportadoId(reportadoId);
            request.setValoracion(valoracion);
            request.setComentario(comentario);
            if (motivos != null) {
                request.setMotivos(new HashSet<>(List.of(motivos)));
            }

            reporteExperienciaProxy.crearReporte(token, partidoId, request);
            redirectAttributes.addFlashAttribute("exito", "Reporte enviado correctamente.");
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            session.invalidate();
            return "redirect:/login";
        } catch (HttpClientErrorException e) {
            String mensaje = e.getResponseBodyAsString();
            redirectAttributes.addFlashAttribute("error",
                    mensaje != null && !mensaje.isBlank() ? mensaje : "No se pudo guardar el reporte.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo guardar el reporte.");
        }

        return "redirect:/perfil/reportes/" + partidoId;
    }

    private Optional<PartidoPendienteReporteDto> obtenerPartido(String token, Long partidoId) {
        List<PartidoPendienteReporteDto> partidos = reporteExperienciaProxy.obtenerPartidosJugados(token);
        if (partidos == null) {
            return Optional.empty();
        }

        return partidos.stream()
                .filter(partido -> partidoId.equals(partido.getPartidoId()))
                .findFirst();
    }
}
