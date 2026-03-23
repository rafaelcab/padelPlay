package com.padelplay.cliente.controller;

import com.padelplay.common.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class RegistroController {

    @GetMapping("/")
    public String home() {
        return "redirect:/registro";
    }

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/api/usuarios/registro";

        String respuesta = restTemplate.postForObject(url, usuario, String.class);

        if ("ERROR".equals(respuesta)) {
            model.addAttribute("error", "Email ya registrado");
            return "registro";
        }

        return "redirect:/login";
    }
}