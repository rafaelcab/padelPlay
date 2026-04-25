package com.padelplay.cliente.controller;

import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}