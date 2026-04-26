package com.padelplay.cliente.controller;

import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PerfilEntrenadorDto;
import com.padelplay.common.dto.SolicitudEntrenamientoDto;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/entrenadores") // Ahora la URL base es /entrenadores
public class EntrenadorWebController {

    private final PerfilServiceProxy perfilServiceProxy;

    public EntrenadorWebController(PerfilServiceProxy perfilServiceProxy) {
        this.perfilServiceProxy = perfilServiceProxy;
    }

    @GetMapping
    public String mostrarEntrenadores(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        
        // 1. Intentamos cargar el estado del perfil para el header (nombre, foto, etc.)
        if (token != null) {
            try {
                EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
                model.addAttribute("estado", estado);
            } catch (Exception ignored) {
                // Si falla el perfil, no bloqueamos la página, solo no se verá el nombre en el header
            }
        }

        // 2. Cargamos la lista de entrenadores desde el microservicio de perfiles
        try {
            var listaEntrenadores = perfilServiceProxy.obtenerEntrenadoresPublicos();
            model.addAttribute("entrenadores", listaEntrenadores);
        } catch (Exception e) {
            model.addAttribute("entrenadores", List.of());
            model.addAttribute("error", "No se pudieron cargar los entrenadores en este momento.");
        }

        return "entrenadores"; // Renderiza entrenadores.html
    }

    @GetMapping("/reservar/{id}")
    public String mostrarFormularioReserva(@PathVariable Long id, Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            model.addAttribute("estado", estado);
            
            // Buscar al entrenador específico de la lista
            List<PerfilEntrenadorDto> entrenadores = perfilServiceProxy.obtenerEntrenadoresPublicos();
            PerfilEntrenadorDto entrenadorSeleccionado = entrenadores.stream()
                    .filter(e -> e.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (entrenadorSeleccionado == null) {
                return "redirect:/entrenadores";
            }

            model.addAttribute("entrenador", entrenadorSeleccionado);
            return "reservar-entrenador";
        } catch (Exception e) {
            return "redirect:/entrenadores";
        }
    }

    @PostMapping("/reservar/{id}")
    public String procesarReserva(@PathVariable Long id, @RequestParam(required = false) String mensaje, Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        SolicitudEntrenamientoDto respuesta = perfilServiceProxy.crearSolicitudEntrenamiento(token, id, mensaje);
        
        if (respuesta.getError() != null) {
            // Recargar la lista y mostrar el error
            try {
                EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
                model.addAttribute("estado", estado);
                model.addAttribute("entrenadores", perfilServiceProxy.obtenerEntrenadoresPublicos());
            } catch (Exception ignored) {}
            
            model.addAttribute("error", respuesta.getError());
            return "entrenadores";
        }

        // Si todo va bien, redirigir a entrenadores con un flag de éxito
        return "redirect:/entrenadores?exito=true";
    }
}