package com.padelplay.server.entity;

/**
 * Tipos de golpes en pádel que un jugador puede dominar.
 */
public enum TipoGolpe {
    SMASH("Smash"),
    VOLEA("Volea"),
    BANDEJA("Bandeja"),
    GLOBO("Globo"),
    VIBORA("Víbora"),
    CHIQUITA("Chiquita"),
    DEJADA("Dejada"),
    SAQUE("Saque"),
    RESTO("Resto"),
    PARED("Golpe de pared");

    private final String descripcion;

    TipoGolpe(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
