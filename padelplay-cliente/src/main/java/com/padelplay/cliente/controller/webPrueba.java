package com.padelplay.cliente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class webPrueba {

    @GetMapping("/prueba")
    public String home(Model model) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/api/test";

        String mensaje = restTemplate.getForObject(url, String.class);

        model.addAttribute("mensaje", mensaje);

        return "prueba";
    }
}