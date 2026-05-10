package com.padelplay.common.dto;

import java.time.LocalDateTime;

/**
 * DTO para crear una solicitud de evaluacion en pista.
 */
public class CrearSolicitudEvaluacionDto {

    private Long entrenadorId;
    private LocalDateTime fechaHoraSolicitada;
    private String comentarioJugador;

    public CrearSolicitudEvaluacionDto() {
    }

    public Long getEntrenadorId() {
        return entrenadorId;
    }

    public void setEntrenadorId(Long entrenadorId) {
        this.entrenadorId = entrenadorId;
    }

    public LocalDateTime getFechaHoraSolicitada() {
        return fechaHoraSolicitada;
    }

    public void setFechaHoraSolicitada(LocalDateTime fechaHoraSolicitada) {
        this.fechaHoraSolicitada = fechaHoraSolicitada;
    }

    public String getComentarioJugador() {
        return comentarioJugador;
    }

    public void setComentarioJugador(String comentarioJugador) {
        this.comentarioJugador = comentarioJugador;
    }
}
