package com.padelplay.cliente.controller;

import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.cliente.proxies.PerfilServiceProxy;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class AuthWebController {

    private final PerfilServiceProxy perfilServiceProxy;
    private final String serverApiUrl;
    private final String googleClientId;

    public AuthWebController(
            PerfilServiceProxy perfilServiceProxy,
            @Value("${server.api.url:http://localhost:8080}") String serverApiUrl,
            @Value("${google.client-id:75813445817-4rvtubv93v7tcc3h7o3t8tu5s8u4tbsn.apps.googleusercontent.com}") String googleClientId) {
        this.perfilServiceProxy = perfilServiceProxy;
        this.serverApiUrl = serverApiUrl;
        this.googleClientId = googleClientId;
    }

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        if (token != null && !token.isBlank()) {
            try {
                EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
                if (estado != null) {
                    return estado.isRequiereSeleccionPerfil()
                            ? "redirect:/perfil/seleccionar-rol"
                            : "redirect:/perfil/dashboard";
                }
            } catch (Exception e) {
                if (e instanceof HttpClientErrorException.Unauthorized
                        || e instanceof HttpClientErrorException.Forbidden) {
                    session.invalidate();
                }
            }
        }

        model.addAttribute("serverApiUrl", serverApiUrl);
        model.addAttribute("googleClientId", googleClientId);
        return "login";
    }
}
