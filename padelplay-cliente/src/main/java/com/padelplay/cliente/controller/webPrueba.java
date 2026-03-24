package com.padelplay.cliente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class webPrueba {

    @GetMapping("/")
    public String home(Model model) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/api/test";

        String mensaje = restTemplate.getForObject(url, String.class);

        model.addAttribute("mensaje", mensaje);

        return "prueba";
    }

    @GetMapping("/registro")
    public String mostrarFormulario() {
        return "registro"; // nombre del HTML (registro.html)
    }  

    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String nombre, @RequestParam String email, @RequestParam String password) {

        System.out.println("Usuario registrado: " + nombre);

        return "redirect:/login";
    }
}