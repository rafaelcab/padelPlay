package com.padelplay.common.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * DTO para los detalles técnico-tácticos del jugador.
 */
public class DetallesTecnicosDto {

    private Long id;
    private String posicion; // DERECHA, REVES, AMBAS
    private String estiloJuego; // AGRESIVO, DEFENSIVO, CONTRAATAQUE, RED
    private Set<String> golpesFuertes = new HashSet<>(); // SMASH, VOLEA, BANDEJA, etc.
    private String observaciones;
    private String manoHabil; // DIESTRO, ZURDO, AMBIDIESTRO

    public DetallesTecnicosDto() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public String getEstiloJuego() {
        return estiloJuego;
    }

    public void setEstiloJuego(String estiloJuego) {
        this.estiloJuego = estiloJuego;
    }

    public Set<String> getGolpesFuertes() {
        return golpesFuertes;
    }

    public void setGolpesFuertes(Set<String> golpesFuertes) {
        this.golpesFuertes = golpesFuertes;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getManoHabil() {
        return manoHabil;
    }

    public void setManoHabil(String manoHabil) {
        this.manoHabil = manoHabil;
    }
}
