package com.padelplay.server.config;

import com.padelplay.server.entity.Jugador;
import com.padelplay.server.repository.JugadorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(JugadorRepository jugadorRepository) {
        return args -> {
            // Solo lo inserta si la base de datos está vacía
            if (jugadorRepository.count() == 0) {
                Jugador paco = new Jugador();
                paco.setNombre("Paco Prueba");
                paco.setEmail("paco@padelplay.com");
                paco.setPassword("123456");
                paco.setNivel(3.5); // Nivel de Paco
                
                jugadorRepository.save(paco);
                System.out.println("✅ Jugador temporal creado en H2 con ID: " + paco.getId());
            }
        };
    }
}