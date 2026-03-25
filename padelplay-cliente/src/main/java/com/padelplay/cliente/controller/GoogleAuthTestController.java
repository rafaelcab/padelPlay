package com.padelplay.cliente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoogleAuthTestController {

    @GetMapping("/google-test")
    public String googleTest() {
        return "google-test";
    }
}
