package com.padelplay.common.dto;

/**
 * DTO para información básica del perfil de jugador.
 */
public class PerfilJugadorDto {

    private Long id;
    private String apodo;
    private Integer aniosExperiencia;
    private String nivelJuego;
    private String telefono;
    private DetallesTecnicosDto detallesTecnicos;

    public PerfilJugadorDto() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public Integer getAniosExperiencia() {
        return aniosExperiencia;
    }

    public void setAniosExperiencia(Integer aniosExperiencia) {
        this.aniosExperiencia = aniosExperiencia;
    }

    public String getNivelJuego() {
        return nivelJuego;
    }

    public void setNivelJuego(String nivelJuego) {
        this.nivelJuego = nivelJuego;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public DetallesTecnicosDto getDetallesTecnicos() {
        return detallesTecnicos;
    }

    public void setDetallesTecnicos(DetallesTecnicosDto detallesTecnicos) {
        this.detallesTecnicos = detallesTecnicos;
    }
}
