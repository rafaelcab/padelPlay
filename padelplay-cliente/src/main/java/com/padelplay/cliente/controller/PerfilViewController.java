package com.padelplay.cliente.controller;

import com.padelplay.common.dto.CertificacionDto;
import com.padelplay.common.dto.DetallesTecnicosDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PartidoDto;
import com.padelplay.common.dto.PartidoPendienteReporteDto;
import com.padelplay.common.dto.PerfilEntrenadorDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.cliente.proxies.ReporteExperienciaProxy;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

/**
 * Controller para las vistas de perfil de usuario.
 */
@Controller
@RequestMapping("/perfil")
public class PerfilViewController {

    private final PerfilServiceProxy perfilServiceProxy;
    private final ReporteExperienciaProxy reporteExperienciaProxy;
    private final RestTemplate restTemplate;

    public PerfilViewController(PerfilServiceProxy perfilServiceProxy,
                                ReporteExperienciaProxy reporteExperienciaProxy,
                                RestTemplate restTemplate) {
        this.perfilServiceProxy = perfilServiceProxy;
        this.reporteExperienciaProxy = reporteExperienciaProxy;
        this.restTemplate = restTemplate;
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

            if (estado.getPerfilJugador() != null) {
                cargarResumenAdmin(model, estado.getPerfilJugador().getId());
            }
            return "dashboard-principal";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "dashboard-principal";
        }
    }

    @GetMapping("/mi-perfil")
    public String miPerfil(@RequestParam(required = false) String token, HttpSession session, Model model) {
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
            model.addAttribute("partidosReportables", cargarPartidosReportables(token, estado));

            if (estado.getPerfilJugador() != null) {
                cargarResumenAdmin(model, estado.getPerfilJugador().getId());
            }

            // Cargar partidos recientes según el rol activo
            if ("ENTRENADOR".equals(estado.getRolActivo())) {
                try {
                    List<PartidoDto> recientesEntrenador = perfilServiceProxy.obtenerPartidosDeAlumnos(token);
                    // Tomar solo los 3 más recientes para el dashboard
                    if (recientesEntrenador.size() > 3) {
                        recientesEntrenador = recientesEntrenador.subList(0, 3);
                    }
                    model.addAttribute("partidosRecientes", recientesEntrenador);
                } catch (Exception e) {
                    model.addAttribute("partidosRecientes", java.util.List.of());
                }
            }

            return "perfil-dashboard";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            model.addAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            model.addAttribute("partidosReportables", List.of());
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

    @PostMapping("/entrenador/guardar-disponibilidad")
    public String guardarDisponibilidadEntrenador(
            @RequestParam(required = false) String dispLunes,
            @RequestParam(required = false) String dispMartes,
            @RequestParam(required = false) String dispMiercoles,
            @RequestParam(required = false) String dispJueves,
            @RequestParam(required = false) String dispViernes,
            @RequestParam(required = false) String dispSabado,
            @RequestParam(required = false) String dispDomingo,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null)
            return "redirect:/login";

        try {
            // Recuperar el perfil existente para no sobreescribir otros campos
            PerfilEntrenadorDto perfil = perfilServiceProxy.obtenerPerfilEntrenador(token);
            if (perfil == null)
                perfil = new PerfilEntrenadorDto();

            perfil.setDispLunes(dispLunes != null ? dispLunes : "");
            perfil.setDispMartes(dispMartes != null ? dispMartes : "");
            perfil.setDispMiercoles(dispMiercoles != null ? dispMiercoles : "");
            perfil.setDispJueves(dispJueves != null ? dispJueves : "");
            perfil.setDispViernes(dispViernes != null ? dispViernes : "");
            perfil.setDispSabado(dispSabado != null ? dispSabado : "");
            perfil.setDispDomingo(dispDomingo != null ? dispDomingo : "");

            perfilServiceProxy.actualizarPerfilEntrenador(token, perfil);
            return "redirect:/perfil/mi-perfil?exito=disponibilidad";
        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            return "redirect:/perfil/mi-perfil?error=" + e.getMessage();
        }
    }

    @PostMapping("/entrenador/finalizar")
    public String finalizarConfiguracionEntrenador(HttpSession session) {
        return "redirect:/perfil/dashboard";
    }

    // =========================================================================
    // DASHBOARD O SECCIÓN PRINCIPAL PARA ENTRENADOR
    // GET /perfil/entrenador
    // =========================================================================
    @GetMapping("/entrenador")
    public String seccionEntrenador(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null)
            return "redirect:/login";

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            if (estado != null && "ENTRENADOR".equals(estado.getRolActivo())) {
                // Si es entrenador, redirigir al historial de partidos
                return "redirect:/perfil/entrenador/historial-partidos";
            } else {
                // Si no es entrenador, redirigir al dashboard general
                return "redirect:/perfil/entrenador/historial-partidos";
            }
        } catch (Exception e) {
            return "redirect:/perfil/dashboard";
        }
    }

    // =========================================================================
    // HISTORIAL DE PARTIDOS PARA ENTRENADOR
    // GET /perfil/entrenador/historial-partidos
    // =========================================================================
    @GetMapping("/entrenador/historial-partidos")
    public String mostrarHistorialPartidosEntrenador(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null)
            return "redirect:/login";

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            model.addAttribute("estado", estado);

            try {
                List<PartidoDto> partidosAlumnos = perfilServiceProxy.obtenerPartidosDeAlumnos(token);
                model.addAttribute("partidos", partidosAlumnos);
            } catch (Exception ex) {
                model.addAttribute("error", "Error al obtener partidos: " + ex.getMessage());
                model.addAttribute("partidos", java.util.List.of());
            }

        } catch (Exception e) {
            if (esSesionInvalida(e))
                return redirigirALogin(session);
            model.addAttribute("error", "Error al cargar el historial: " + e.getMessage());
            model.addAttribute("partidos", java.util.List.of());
        }

        return "entrenador-historial-partidos";
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

    private void cargarResumenAdmin(Model model, Long perfilJugadorId) {
        try {
            ResponseEntity<PartidoDto[]> response = restTemplate.getForEntity("http://localhost:8080/api/partidos",
                    PartidoDto[].class);
            List<PartidoDto> partidos = java.util.Arrays
                    .asList(response.getBody() != null ? response.getBody() : new PartidoDto[0]);

            long creados = partidos.stream()
                    .filter(partido -> partido.getCreador() != null
                            && perfilJugadorId.equals(partido.getCreador().getId()))
                    .count();
            long activos = partidos.stream()
                    .filter(partido -> partido.getCreador() != null
                            && perfilJugadorId.equals(partido.getCreador().getId()))
                    .filter(partido -> !partido.isCancelado())
                    .filter(partido -> partido.getFechaHora() != null
                            && partido.getFechaHora().isAfter(LocalDateTime.now().minusMinutes(5)))
                    .count();
            long cancelados = partidos.stream()
                    .filter(partido -> partido.getCreador() != null
                            && perfilJugadorId.equals(partido.getCreador().getId()))
                    .filter(PartidoDto::isCancelado)
                    .count();

            model.addAttribute("adminPartidosCreados", creados);
            model.addAttribute("adminPartidosActivos", activos);
            model.addAttribute("adminPartidosCancelados", cancelados);
        } catch (Exception ignored) {
            model.addAttribute("adminPartidosCreados", 0L);
            model.addAttribute("adminPartidosActivos", 0L);
            model.addAttribute("adminPartidosCancelados", 0L);
        }
    }

    private List<PartidoPendienteReporteDto> cargarPartidosReportables(String token, EstadoPerfilDto estado) {
        if (estado == null || estado.getPerfilJugador() == null) {
            return List.of();
        }

        try {
            List<PartidoPendienteReporteDto> partidos = reporteExperienciaProxy.obtenerPartidosJugados(token);
            return partidos != null ? partidos : List.of();
        } catch (Exception ignored) {
            return List.of();
        }
    }
}
