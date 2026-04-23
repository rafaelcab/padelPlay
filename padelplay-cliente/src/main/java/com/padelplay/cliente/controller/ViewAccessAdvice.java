package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.common.dto.EstadoPerfilDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class ViewAccessAdvice {

    private final PerfilServiceProxy perfilServiceProxy;
    private final Set<String> adminClubEmails;

    public ViewAccessAdvice(
            PerfilServiceProxy perfilServiceProxy,
            @Value("${club.admin.emails:}") String adminEmailsProperty) {
        this.perfilServiceProxy = perfilServiceProxy;
        this.adminClubEmails = Arrays.stream(adminEmailsProperty.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @ModelAttribute("esAdminClub")
    public boolean esAdminClub(HttpSession session) {
        EstadoPerfilDto estado = obtenerEstado(session);
        if (estado == null || estado.getEmail() == null) {
            return false;
        }
        return adminClubEmails.contains(estado.getEmail().trim().toLowerCase());
    }

    @ModelAttribute("estado")
    public EstadoPerfilDto estado(HttpSession session) {
        return obtenerEstado(session);
    }

    private EstadoPerfilDto obtenerEstado(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            return perfilServiceProxy.obtenerEstadoPerfil(token);
        } catch (Exception ignored) {
            return null;
        }
    }
}
