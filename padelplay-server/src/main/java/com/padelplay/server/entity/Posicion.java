package com.padelplay.server.entity;

/**
 * Posición de juego preferida del jugador en la pista.
 */
public enum Posicion {
    DERECHA("Derecha"),
    REVES("Revés"),
    AMBAS("Ambas");

    private final String descripcion;

    Posicion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
