package com.padelplay.common.dto;

/**
 * DTO sencillo para listar entrenadores disponibles para evaluaciones.
 */
public class EntrenadorDisponibleDto {

    private Long id;
    private String nombre;
    private String email;
    private String fotoUrl;

    public EntrenadorDisponibleDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
