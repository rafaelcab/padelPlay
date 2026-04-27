package com.padelplay.common.dto;

public class ParticipantePendienteReporteDto {

    private Long perfilJugadorId;
    private String apodo;

    public ParticipantePendienteReporteDto() {
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
}
