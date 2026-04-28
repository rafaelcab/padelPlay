package com.padelplay.common.dto;

import java.time.LocalDateTime;

public class SolicitudEntrenamientoDto {

    private Long id;
    private Long jugadorId;
    private Long entrenadorId;
    private String mensaje;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private String error;

    public SolicitudEntrenamientoDto() {
    }

    public SolicitudEntrenamientoDto(String error) {
        this.error = error;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }

    public Long getEntrenadorId() {
        return entrenadorId;
    }

    public void setEntrenadorId(Long entrenadorId) {
        this.entrenadorId = entrenadorId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
