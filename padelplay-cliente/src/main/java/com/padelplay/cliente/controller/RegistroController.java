package com.padelplay.cliente.controller;

import com.padelplay.common.dto.AuthResponseDto;
import com.padelplay.common.dto.RegistroRequestDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class RegistroController {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public RegistroController(@Value("${server.api.url:http://localhost:8080}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/registro";
    }

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("registro", new RegistroRequestDto());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @ModelAttribute("registro") RegistroRequestDto registro,
            HttpSession session,
            Model model) {
        try {
            ResponseEntity<AuthResponseDto> response = restTemplate.postForEntity(
                    serverUrl + "/api/auth/register",
                    registro,
                    AuthResponseDto.class
            );

            AuthResponseDto auth = response.getBody();
            if (auth == null || auth.getToken() == null || auth.getToken().isBlank()) {
                model.addAttribute("error", "No se pudo completar el registro.");
                return "registro";
            }

            session.setAttribute("token", auth.getToken());
            if (auth.isNuevoUsuario()) {
                return "redirect:/perfil/seleccionar-rol?token=" + auth.getToken();
            }
            return "redirect:/perfil/dashboard?token=" + auth.getToken();
        } catch (HttpClientErrorException ex) {
            String mensaje = "No se pudo completar el registro.";
            if (ex.getStatusCode().value() == 409) {
                mensaje = "Email ya registrado";
            } else if (ex.getStatusCode().value() == 400) {
                mensaje = "Completa todos los campos";
            }
            model.addAttribute("error", mensaje);
            return "registro";
        } catch (Exception ex) {
            model.addAttribute("error", "Error inesperado al registrar");
            return "registro";
        }
    }
}
