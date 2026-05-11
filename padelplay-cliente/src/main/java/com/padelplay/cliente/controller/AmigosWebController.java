package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.AmigosServiceProxy;
import com.padelplay.common.dto.AmigoPerfilDto;
import com.padelplay.common.dto.PartidosJugadosPublicosCursorDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/comunidad")
public class AmigosWebController {

    private final AmigosServiceProxy amigosServiceProxy;
    private final com.padelplay.cliente.proxies.PerfilServiceProxy perfilServiceProxy;

    public AmigosWebController(AmigosServiceProxy amigosServiceProxy, com.padelplay.cliente.proxies.PerfilServiceProxy perfilServiceProxy) {
        this.amigosServiceProxy = amigosServiceProxy;
        this.perfilServiceProxy = perfilServiceProxy;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null || token.isBlank()) {
            return "redirect:/login";
        }

        try {
            List<AmigoPerfilDto> amigos = amigosServiceProxy.listarAmigos(token);
            model.addAttribute("amigos", amigos);
            
            // También cargamos todos los entrenadores públicos
            List<com.padelplay.common.dto.PerfilEntrenadorDto> entrenadores = perfilServiceProxy.obtenerEntrenadoresPublicos();
            model.addAttribute("entrenadores", entrenadores);
        } catch (Exception e) {
            model.addAttribute("amigos", List.of());
            model.addAttribute("entrenadores", List.of());
            model.addAttribute("error", "No se pudieron cargar los perfiles.");
        }

        return "comunidad";
    }

    @GetMapping("/{usuarioId}")
    public String detalle(@PathVariable Long usuarioId, Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null || token.isBlank()) {
            return "redirect:/login";
        }

        try {
            AmigoPerfilDto amigo = amigosServiceProxy.obtenerAmigo(token, usuarioId);
            model.addAttribute("amigo", amigo);
            if (amigo.isTienePerfilJugador()) {
                model.addAttribute("trayectoriaPreview",
                        amigosServiceProxy.obtenerPartidosJugadosPublicos(token, usuarioId, 3, null, "next"));
            }
            return "amigo-detalle";
        } catch (Exception e) {
            return "redirect:/comunidad?error=not-found";
        }
    }

    @GetMapping("/{usuarioId}/trayectoria")
    public String trayectoria(@PathVariable Long usuarioId,
                              @RequestParam(value = "cursor", required = false) String cursor,
                              @RequestParam(value = "direction", defaultValue = "next") String direction,
                              Model model,
                              HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null || token.isBlank()) {
            return "redirect:/login";
        }

        try {
            AmigoPerfilDto amigo = amigosServiceProxy.obtenerAmigo(token, usuarioId);
            PartidosJugadosPublicosCursorDto trayectoria = amigosServiceProxy.obtenerPartidosJugadosPublicos(
                    token,
                    usuarioId,
                    10,
                    cursor,
                    direction
            );
            model.addAttribute("amigo", amigo);
            model.addAttribute("trayectoria", trayectoria);
            return "amigo-trayectoria";
        } catch (Exception e) {
            return "redirect:/comunidad?error=not-found";
        }
    }

    @PostMapping("/{usuarioId}/seguir")
    public String seguir(@PathVariable Long usuarioId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null || token.isBlank()) {
            return "redirect:/login";
        }

        try {
            amigosServiceProxy.seguir(token, usuarioId);
            redirectAttributes.addFlashAttribute("exito", "Ahora sigues a este perfil.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo seguir al perfil.");
        }

        return "redirect:/comunidad/" + usuarioId;
    }
}
