package com.padelplay.common.dto;

import java.time.LocalDateTime;

public class PartidoPendienteReporteDto {

    private Long partidoId;
    private LocalDateTime fechaHora;
    private String ubicacion;
    private String tipoPartido;
    private Integer participantesPendientes;
    private boolean reporteCompletado;
    private String resultado;
    private boolean resultadoConfirmado;
    private boolean resultadoRechazado;

    public PartidoPendienteReporteDto() {
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

    public Integer getParticipantesPendientes() {
        return participantesPendientes;
    }

    public void setParticipantesPendientes(Integer participantesPendientes) {
        this.participantesPendientes = participantesPendientes;
    }

    public boolean isReporteCompletado() {
        return reporteCompletado;
    }

    public void setReporteCompletado(boolean reporteCompletado) {
        this.reporteCompletado = reporteCompletado;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public boolean isResultadoConfirmado() {
        return resultadoConfirmado;
    }

    public void setResultadoConfirmado(boolean resultadoConfirmado) {
        this.resultadoConfirmado = resultadoConfirmado;
    }

    public boolean isResultadoRechazado() {
        return resultadoRechazado;
    }

    public void setResultadoRechazado(boolean resultadoRechazado) {
        this.resultadoRechazado = resultadoRechazado;
    }
}
