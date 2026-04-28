package com.padelplay.common.dto;

import java.time.LocalDateTime;

public class ResultadoPartidoGestionCreadorDto {

    private Long partidoId;
    private LocalDateTime fechaHora;
    private String ubicacion;
    private String tipoPartido;
    private boolean puedeRegistrarResultado;
    private boolean resultadoPendienteValidacion;
    private boolean resultadoRechazado;
    private boolean resultadoValidado;

    public ResultadoPartidoGestionCreadorDto() {
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
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

    public boolean isPuedeRegistrarResultado() {
        return puedeRegistrarResultado;
    }

    public void setPuedeRegistrarResultado(boolean puedeRegistrarResultado) {
        this.puedeRegistrarResultado = puedeRegistrarResultado;
    }

    public boolean isResultadoPendienteValidacion() {
        return resultadoPendienteValidacion;
    }

    public void setResultadoPendienteValidacion(boolean resultadoPendienteValidacion) {
        this.resultadoPendienteValidacion = resultadoPendienteValidacion;
    }

    public boolean isResultadoRechazado() {
        return resultadoRechazado;
    }

    public void setResultadoRechazado(boolean resultadoRechazado) {
        this.resultadoRechazado = resultadoRechazado;
    }

    public boolean isResultadoValidado() {
        return resultadoValidado;
    }

    public void setResultadoValidado(boolean resultadoValidado) {
        this.resultadoValidado = resultadoValidado;
    }
}
