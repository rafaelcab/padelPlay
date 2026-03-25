package com.padelplay.cliente.controller;

import com.padelplay.common.dto.PartidoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/partidos")
public class PartidoWebController {

    private final RestTemplate restTemplate;
    
    // Ruta de tu backend
    private final String BACKEND_URL = "http://localhost:8080/api/partidos";

    public PartidoWebController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // =========================================================================
    // 1. MOSTRAR EL FORMULARIO
    // =========================================================================
    @GetMapping("/crear")
    public String mostrarFormulario(Model model) {
        model.addAttribute("partido", new PartidoDto());
        return "crear-partido"; 
    }

    // =========================================================================
    // 2. RECIBIR EL FORMULARIO Y ENVIAR AL BACKEND
    // =========================================================================
    @PostMapping("/crear")
    public String procesarFormulario(@ModelAttribute("partido") PartidoDto partidoDto, RedirectAttributes redirectAttributes, Model model) {
        try {
            // Hacemos la petición al servidor y guardamos la respuesta
            ResponseEntity<PartidoDto> respuestaServidor = restTemplate.postForEntity(BACKEND_URL, partidoDto, PartidoDto.class);
            PartidoDto partidoCreado = respuestaServidor.getBody();

            // === LA MAGIA: Metemos el partido entero en la "Mochila Flash" ===
            // Esta mochila sobrevive a la redirección y le pasa todos los datos a exito.html
            redirectAttributes.addFlashAttribute("partido", partidoCreado);

            return "redirect:/partidos/exito";

        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "No se pudo crear el partido. Revisa los datos introducidos.");
            return "crear-partido"; 
        }
    }

    // =========================================================================
    // 3. NUEVA PANTALLA DE ÉXITO
    // =========================================================================
    @GetMapping("/exito")
    public String mostrarExito(Model model) {
        // Truco de seguridad: Si no hay partido en la mochila (ej. alguien entra a /exito directamente), lo echamos a /crear
        if (!model.containsAttribute("partido")) {
            return "redirect:/partidos/crear";
        }
        
        return "exito"; 
    }
}