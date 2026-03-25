package com.padelplay.cliente.controller;

import com.padelplay.common.dto.DetallesTecnicosDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

/**
 * Controller para las vistas de perfil de usuario.
 */
@Controller
@RequestMapping("/perfil")
public class PerfilViewController {

    private final PerfilServiceProxy perfilServiceProxy;

    public PerfilViewController(PerfilServiceProxy perfilServiceProxy) {
        this.perfilServiceProxy = perfilServiceProxy;
    }

    /**
     * Página de selección de rol (para usuarios nuevos).
     */
    @GetMapping("/seleccionar-rol")
    public String paginaSeleccionRol(@RequestParam(required = false) String token, HttpSession session, Model model) {
        // Obtener token del parámetro o de la sesión
        if (token != null && !token.isBlank()) {
            session.setAttribute("token", token);
        } else {
            token = (String) session.getAttribute("token");
        }
        
        if (token == null) {
            return "redirect:/google-test";
        }
        
        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            
            // Si ya tiene perfiles, redirigir al dashboard
            if (!estado.isRequiereSeleccionPerfil()) {
                return "redirect:/perfil/dashboard";
            }
            
            model.addAttribute("usuario", estado);
            return "seleccion-rol";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            return "seleccion-rol";
        }
    }

    /**
     * Procesa la selección de rol.
     */
    @PostMapping("/seleccionar-rol")
    public String procesarSeleccionRol(
            @RequestParam String rol,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/google-test";
        }
        
        try {
            perfilServiceProxy.seleccionarRol(token, rol);
            
            if ("JUGADOR".equals(rol)) {
                return "redirect:/perfil/jugador/configurar";
            } else {
                return "redirect:/perfil/dashboard";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al seleccionar rol: " + e.getMessage());
            return "seleccion-rol";
        }
    }

    /**
     * Dashboard del perfil del usuario.
     */
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String token, HttpSession session, Model model) {
        // Obtener token del parámetro o de la sesión
        if (token != null && !token.isBlank()) {
            session.setAttribute("token", token);
        } else {
            token = (String) session.getAttribute("token");
        }
        
        if (token == null) {
            return "redirect:/google-test";
        }
        
        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            
            // Si requiere selección de perfil, redirigir
            if (estado.isRequiereSeleccionPerfil()) {
                return "redirect:/perfil/seleccionar-rol";
            }
            
            model.addAttribute("estado", estado);
            return "perfil-dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "perfil-dashboard";
        }
    }

    /**
     * Página de configuración del perfil de jugador.
     */
    @GetMapping("/jugador/configurar")
    public String configurarPerfilJugador(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/google-test";
        }
        
        try {
            PerfilJugadorDto perfil = perfilServiceProxy.obtenerPerfilJugador(token);
            Map<String, Object> opciones = perfilServiceProxy.obtenerOpcionesTecnicas();
            
            model.addAttribute("perfil", perfil);
            model.addAttribute("opciones", opciones);
            return "perfil-jugador";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            return "perfil-jugador";
        }
    }

    /**
     * Guarda el perfil de jugador.
     */
    @PostMapping("/jugador/guardar")
    public String guardarPerfilJugador(
            @ModelAttribute PerfilJugadorDto perfil,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/google-test";
        }
        
        try {
            perfilServiceProxy.actualizarPerfilJugador(token, perfil);
            return "redirect:/perfil/jugador/detalles-tecnicos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el perfil: " + e.getMessage());
            model.addAttribute("perfil", perfil);
            return "perfil-jugador";
        }
    }

    /**
     * Página de configuración de detalles técnicos.
     */
    @GetMapping("/jugador/detalles-tecnicos")
    public String configurarDetallesTecnicos(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/google-test";
        }
        
        try {
            PerfilJugadorDto perfil = perfilServiceProxy.obtenerPerfilJugador(token);
            Map<String, Object> opciones = perfilServiceProxy.obtenerOpcionesTecnicas();
            
            model.addAttribute("perfil", perfil);
            model.addAttribute("detalles", perfil.getDetallesTecnicos() != null ? 
                    perfil.getDetallesTecnicos() : new DetallesTecnicosDto());
            model.addAttribute("opciones", opciones);
            return "detalles-tecnicos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los detalles: " + e.getMessage());
            return "detalles-tecnicos";
        }
    }

    /**
     * Guarda los detalles técnicos.
     */
    @PostMapping("/jugador/detalles-tecnicos/guardar")
    public String guardarDetallesTecnicos(
            @ModelAttribute DetallesTecnicosDto detalles,
            @RequestParam(required = false) String[] golpesFuertesArray,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/google-test";
        }
        
        try {
            // Convertir array de golpes a Set
            if (golpesFuertesArray != null) {
                detalles.setGolpesFuertes(new java.util.HashSet<>(java.util.Arrays.asList(golpesFuertesArray)));
            }
            
            perfilServiceProxy.actualizarDetallesTecnicos(token, detalles);
            return "redirect:/perfil/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar los detalles: " + e.getMessage());
            model.addAttribute("detalles", detalles);
            return "detalles-tecnicos";
        }
    }

    /**
     * Cambia el rol activo del usuario.
     */
    @PostMapping("/cambiar-rol")
    public String cambiarRol(
            @RequestParam String rol,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/google-test";
        }
        
        try {
            perfilServiceProxy.cambiarRol(token, rol);
            return "redirect:/perfil/dashboard";
        } catch (Exception e) {
            return "redirect:/perfil/dashboard?error=" + e.getMessage();
        }
    }

    /**
     * Añade un nuevo perfil al usuario.
     */
    @PostMapping("/crear-perfil")
    public String crearPerfil(
            @RequestParam String rol,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/google-test";
        }
        
        try {
            perfilServiceProxy.crearPerfilAdicional(token, rol);
            
            if ("JUGADOR".equals(rol)) {
                return "redirect:/perfil/jugador/configurar";
            }
            return "redirect:/perfil/dashboard";
        } catch (Exception e) {
            return "redirect:/perfil/dashboard?error=" + e.getMessage();
        }
    }
}
