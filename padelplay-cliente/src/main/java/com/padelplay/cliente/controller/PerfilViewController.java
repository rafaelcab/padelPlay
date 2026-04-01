package com.padelplay.cliente.controller;

import com.padelplay.common.dto.CertificacionDto;
import com.padelplay.common.dto.DetallesTecnicosDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PerfilEntrenadorDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        if (token != null && !token.isBlank()) {
            session.setAttribute("token", token);
        } else {
            token = (String) session.getAttribute("token");
        }

        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            if (!estado.isRequiereSeleccionPerfil()) {
                return "redirect:/perfil/dashboard";
            }
            model.addAttribute("usuario", estado);
            return "seleccion-rol";
        } catch (Exception e) {
            if (esSesionInvalida(e)) {
                return redirigirALogin(session);
            }
            model.addAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            return "seleccion-rol";
        }
    }

    @PostMapping("/seleccionar-rol")
    public String procesarSeleccionRol(@RequestParam String rol, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            perfilServiceProxy.seleccionarRol(token, rol);
            if ("JUGADOR".equals(rol)) {
                return "redirect:/perfil/jugador/configurar";
            } else if ("ENTRENADOR".equals(rol)) {
                return "redirect:/perfil/entrenador/configurar";
            } else {
                return "redirect:/perfil/dashboard";
            }
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            model.addAttribute("error", "Error al seleccionar rol: " + e.getMessage());
            return "seleccion-rol";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String token, HttpSession session, Model model) {
        if (token != null && !token.isBlank()) {
            session.setAttribute("token", token);
        } else {
            token = (String) session.getAttribute("token");
        }

        if (token == null)
            return "redirect:/login";

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            if (estado.isRequiereSeleccionPerfil()) {
                return "redirect:/perfil/seleccionar-rol";
            }
            model.addAttribute("estado", estado);
            return "perfil-dashboard";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "perfil-dashboard";
        }
    }

    // === MÉTODOS JUGADOR ===

    @GetMapping("/jugador/configurar")
    public String configurarPerfilJugador(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            PerfilJugadorDto perfil = perfilServiceProxy.obtenerPerfilJugador(token);
            Map<String, Object> opciones = perfilServiceProxy.obtenerOpcionesTecnicas();
            model.addAttribute("perfil", perfil);
            model.addAttribute("opciones", opciones);
            return "perfil-jugador";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            model.addAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            return "perfil-jugador";
        }
    }

    @PostMapping("/jugador/guardar")
    public String guardarPerfilJugador(@ModelAttribute PerfilJugadorDto perfil, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            perfilServiceProxy.actualizarPerfilJugador(token, perfil);
            return "redirect:/perfil/jugador/detalles-tecnicos";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            model.addAttribute("error", "Error al guardar el perfil: " + e.getMessage());
            model.addAttribute("perfil", perfil);
            return "perfil-jugador";
        }
    }

    @GetMapping("/jugador/detalles-tecnicos")
    public String configurarDetallesTecnicos(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            PerfilJugadorDto perfil = perfilServiceProxy.obtenerPerfilJugador(token);
            Map<String, Object> opciones = perfilServiceProxy.obtenerOpcionesTecnicas();
            model.addAttribute("perfil", perfil);
            model.addAttribute("detalles",
                    perfil.getDetallesTecnicos() != null ? perfil.getDetallesTecnicos() : new DetallesTecnicosDto());
            model.addAttribute("opciones", opciones);
            return "detalles-tecnicos";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            model.addAttribute("error", "Error al cargar los detalles: " + e.getMessage());
            return "detalles-tecnicos";
        }
    }

    @PostMapping("/jugador/detalles-tecnicos/guardar")
    public String guardarDetallesTecnicos(@ModelAttribute DetallesTecnicosDto detalles,
            @RequestParam(required = false) String[] golpesFuertesArray, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            if (golpesFuertesArray != null) {
                detalles.setGolpesFuertes(new HashSet<>(java.util.Arrays.asList(golpesFuertesArray)));
            }
            perfilServiceProxy.actualizarDetallesTecnicos(token, detalles);
            return "redirect:/perfil/dashboard";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            model.addAttribute("error", "Error al guardar los detalles: " + e.getMessage());
            model.addAttribute("detalles", detalles);
            return "detalles-tecnicos";
        }
    }

    @PostMapping("/cambiar-rol")
    public String cambiarRol(@RequestParam String rol, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            perfilServiceProxy.cambiarRol(token, rol);
            return "redirect:/perfil/dashboard";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            return "redirect:/perfil/dashboard?error=" + e.getMessage();
        }
    }

    @PostMapping("/crear-perfil")
    public String crearPerfil(@RequestParam String rol, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            perfilServiceProxy.crearPerfilAdicional(token, rol);
            if ("JUGADOR".equals(rol))
                return "redirect:/perfil/jugador/configurar";
            if ("ENTRENADOR".equals(rol))
                return "redirect:/perfil/entrenador/configurar";
            return "redirect:/perfil/dashboard";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            return "redirect:/perfil/dashboard?error=" + e.getMessage();
        }
    }

    // === MÉTODOS PARA ENTRENADOR ===

    @GetMapping("/entrenador/configurar")
    public String configurarPerfilEntrenador(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            Map<String, Object> opciones = perfilServiceProxy.obtenerOpcionesEntrenador();
            model.addAttribute("opciones", opciones);
        } catch (Exception e) {
            model.addAttribute("opciones", new java.util.HashMap<>());
        }

        try {
            PerfilEntrenadorDto perfil = perfilServiceProxy.obtenerPerfilEntrenador(token);
            model.addAttribute("perfil", perfil != null ? perfil : new PerfilEntrenadorDto());
        } catch (Exception e) {
            model.addAttribute("perfil", new PerfilEntrenadorDto());
        }
        return "perfil-entrenador";
    }

    @PostMapping("/entrenador/guardar")
    public String guardarPerfilEntrenador(@ModelAttribute PerfilEntrenadorDto perfil,
            @RequestParam(required = false) String[] especialidadesArray, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            if (especialidadesArray != null) {
                perfil.setEspecialidades(new HashSet<>(java.util.Arrays.asList(especialidadesArray)));
            }
            perfilServiceProxy.actualizarPerfilEntrenador(token, perfil);
            return "redirect:/perfil/entrenador/certificaciones";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el perfil: " + e.getMessage());
            model.addAttribute("perfil", perfil);
            model.addAttribute("opciones", perfilServiceProxy.obtenerOpcionesEntrenador());
            return "perfil-entrenador";
        }
    }

    @GetMapping("/entrenador/certificaciones")
    public String gestionarCertificaciones(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            Map<String, Object> opciones = perfilServiceProxy.obtenerOpcionesEntrenador();
            model.addAttribute("opciones", opciones);
            PerfilEntrenadorDto perfil = perfilServiceProxy.obtenerPerfilEntrenador(token);
            model.addAttribute("perfil", perfil);
            model.addAttribute("certificaciones",
                    perfil != null && perfil.getCertificaciones() != null ? perfil.getCertificaciones()
                            : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("perfil", new PerfilEntrenadorDto());
            model.addAttribute("certificaciones", new ArrayList<>());
        }

        model.addAttribute("nuevaCertificacion", new CertificacionDto());
        return "certificaciones-entrenador";
    }

    @PostMapping("/entrenador/certificaciones/agregar")
    public String agregarCertificacion(@ModelAttribute CertificacionDto certificacion, HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            perfilServiceProxy.agregarCertificacion(token, certificacion);
            return "redirect:/perfil/entrenador/certificaciones";
        } catch (Exception e) {
            return "redirect:/perfil/entrenador/certificaciones?error=" + e.getMessage();
        }
    }

    @PostMapping("/entrenador/certificaciones/eliminar/{id}")
    public String eliminarCertificacion(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            perfilServiceProxy.eliminarCertificacion(token, id);
            return "redirect:/perfil/entrenador/certificaciones";
        } catch (Exception e) {
            return "redirect:/perfil/entrenador/certificaciones?error=" + e.getMessage();
        }
    }

    @PostMapping("/entrenador/finalizar")
    public String finalizarConfiguracionEntrenador(HttpSession session) {
        return "redirect:/perfil/dashboard";
    }

    // === MÉTODOS PRIVADOS DE UTILIDAD (Unificados) ===

    private boolean esSesionInvalida(Exception e) {
        return e instanceof HttpClientErrorException.Unauthorized
                || e instanceof HttpClientErrorException.Forbidden;
    }

    private String redirigirALogin(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}