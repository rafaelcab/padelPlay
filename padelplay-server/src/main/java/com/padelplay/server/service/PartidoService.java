package com.padelplay.server.service;

import com.padelplay.common.dto.PartidoDto;
import com.padelplay.server.entity.Jugador;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.repository.JugadorRepository;
import com.padelplay.server.repository.PartidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final JugadorRepository jugadorRepository;

    // Inyección de dependencias a través del constructor
    public PartidoService(PartidoRepository partidoRepository, JugadorRepository jugadorRepository) {
        this.partidoRepository = partidoRepository;
        this.jugadorRepository = jugadorRepository;
    }

    public Partido crearPartido(PartidoDto dto) {
        // 1. Buscar al Creador
        Jugador creador = jugadorRepository.findById(dto.getIdCreador())
                .orElseThrow(() -> new IllegalArgumentException("El jugador con ID " + dto.getIdCreador() + " no existe."));

        // 2. Validar Reglas de Negocio
        if (dto.getNivelRequerido() < 1.0 || dto.getNivelRequerido() > 5.0) {
            throw new IllegalArgumentException("El nivel requerido debe estar entre 1.0 y 5.0");
        }
        
        if (dto.getFechaHora() == null || dto.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha del partido debe ser en el futuro.");
        }

        // 3. Transformación (Mapping)
        Partido partido = new Partido();
        partido.setFechaHora(dto.getFechaHora());
        partido.setUbicacion(dto.getUbicacion());
        partido.setNivelRequerido(dto.getNivelRequerido());
        
        // === NUEVA LÓGICA DE PARTIDO PRIVADO VS ABIERTO ===
        // Leemos el valor que viene desde el HTML ("open" o "private")
        if ("private".equalsIgnoreCase(dto.getTipoPartido())) {
            partido.setTipoPartido("PRIVADO");
            partido.setCodigoAcceso(generarCodigoAleatorio()); // La app genera el código automáticamente
        } else {
            partido.setTipoPartido("ABIERTO");
            partido.setCodigoAcceso(null); // Los abiertos no tienen contraseña
        }
        
        // Siempre nacen con 3 huecos libres (el creador ya ocupa uno)
        partido.setHuecosDisponibles(3); 

        // 4. Vincular las Relaciones
        partido.setCreador(creador);
        
        // Inicializamos la lista de apuntados y metemos al creador
        partido.setJugadoresApuntados(new ArrayList<>());
        partido.getJugadoresApuntados().add(creador);

        // 5. Guardar en Base de Datos y devolver el resultado
        return partidoRepository.save(partido);
    }

    // === HERRAMIENTA AUXILIAR ===
    // Método privado para generar un código alfanumérico de 6 caracteres
    private String generarCodigoAleatorio() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        while (codigo.length() < 6) { 
            int index = (int) (rnd.nextFloat() * caracteres.length());
            codigo.append(caracteres.charAt(index));
        }
        return codigo.toString();
    }
}