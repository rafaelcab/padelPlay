package com.padelplay.common.dto;

/**
 * DTO para aceptar o rechazar una solicitud de evaluacion en pista.
 */
public class ResponderSolicitudEvaluacionDto {

    private String estado;
    private String comentarioEntrenador;

    public ResponderSolicitudEvaluacionDto() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComentarioEntrenador() {
        return comentarioEntrenador;
    }

    public void setComentarioEntrenador(String comentarioEntrenador) {
        this.comentarioEntrenador = comentarioEntrenador;
    }
}
