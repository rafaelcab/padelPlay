package com.padelplay.common.dto;

import java.time.LocalDateTime;

public class PartidoPendienteReporteDto {

    private Long id;
    private LocalDateTime fechaHora;
    private String ubicacion;
    private String tipoPartido;
    private Integer participantesPendientes;

    public PartidoPendienteReporteDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipoPartido() {
        return tipoPartido;
    }

    public void setTipoPartido(String tipoPartido) {
        this.tipoPartido = tipoPartido;
    }

    public Integer getParticipantesPendientes() {
        return participantesPendientes;
    }

    public void setParticipantesPendientes(Integer participantesPendientes) {
        this.participantesPendientes = participantesPendientes;
    }
}
