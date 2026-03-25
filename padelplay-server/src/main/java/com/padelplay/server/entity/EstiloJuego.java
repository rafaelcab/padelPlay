package com.padelplay.server.entity;

/**
 * Estilo de juego predominante del jugador.
 */
public enum EstiloJuego {
    AGRESIVO("Agresivo"),
    DEFENSIVO("Defensivo"),
    CONTRAATAQUE("Contraataque"),
    RED("Juego de Red");

    private final String descripcion;

    EstiloJuego(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
