package com.padelplay.common.dto;

/**
 * DTO para completar una evaluacion en pista y proponer el nuevo ELO.
 */
public class CompletarEvaluacionDto {

    private Long solicitudId;
    private Integer nuevoElo;
    private String observaciones;
    private Integer control;
    private Integer potencia;
    private Integer consistencia;
    private Integer posicionamiento;
    private Integer tactica;

    public CompletarEvaluacionDto() {
    }

    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public Integer getNuevoElo() {
        return nuevoElo;
    }

    public void setNuevoElo(Integer nuevoElo) {
        this.nuevoElo = nuevoElo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getControl() {
        return control;
    }

    public void setControl(Integer control) {
        this.control = control;
    }

    public Integer getPotencia() {
        return potencia;
    }

    public void setPotencia(Integer potencia) {
        this.potencia = potencia;
    }

    public Integer getConsistencia() {
        return consistencia;
    }

    public void setConsistencia(Integer consistencia) {
        this.consistencia = consistencia;
    }

    public Integer getPosicionamiento() {
        return posicionamiento;
    }

    public void setPosicionamiento(Integer posicionamiento) {
        this.posicionamiento = posicionamiento;
    }

    public Integer getTactica() {
        return tactica;
    }

    public void setTactica(Integer tactica) {
        this.tactica = tactica;
    }
}
