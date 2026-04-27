package com.padelplay.common.dto;

import java.time.LocalDateTime;

public class ValidacionResultadoPartidoDto {

    private Long perfilJugadorId;
    private String apodo;
    private boolean aceptado;
    private LocalDateTime fechaValidacion;

    public ValidacionResultadoPartidoDto() {
    }

    public Long getPerfilJugadorId() {
        return perfilJugadorId;
    }

    public void setPerfilJugadorId(Long perfilJugadorId) {
        this.perfilJugadorId = perfilJugadorId;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public boolean isAceptado() {
        return aceptado;
    }

    public void setAceptado(boolean aceptado) {
        this.aceptado = aceptado;
    }

    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }
}
