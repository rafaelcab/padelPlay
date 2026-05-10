package com.padelplay.common.dto;

import java.time.LocalDateTime;

/**
 * DTO para representar una solicitud de evaluacion en pista.
 */
public class SolicitudEvaluacionDto {

    private Long id;
    private Long jugadorId;
    private Long entrenadorId;
    private String nombreJugador;
    private String nombreEntrenador;
    private LocalDateTime fechaHoraSolicitada;
    private String estado;
    private String comentarioJugador;
    private String comentarioEntrenador;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaRespuesta;
    private Integer eloActual;
    private Integer nuevoElo;

    public SolicitudEvaluacionDto() {
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

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public String getNombreEntrenador() {
        return nombreEntrenador;
    }

    public void setNombreEntrenador(String nombreEntrenador) {
        this.nombreEntrenador = nombreEntrenador;
    }

    public LocalDateTime getFechaHoraSolicitada() {
        return fechaHoraSolicitada;
    }

    public void setFechaHoraSolicitada(LocalDateTime fechaHoraSolicitada) {
        this.fechaHoraSolicitada = fechaHoraSolicitada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComentarioJugador() {
        return comentarioJugador;
    }

    public void setComentarioJugador(String comentarioJugador) {
        this.comentarioJugador = comentarioJugador;
    }

    public String getComentarioEntrenador() {
        return comentarioEntrenador;
    }

    public void setComentarioEntrenador(String comentarioEntrenador) {
        this.comentarioEntrenador = comentarioEntrenador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public Integer getEloActual() {
        return eloActual;
    }

    public void setEloActual(Integer eloActual) {
        this.eloActual = eloActual;
    }

    public Integer getNuevoElo() {
        return nuevoElo;
    }

    public void setNuevoElo(Integer nuevoElo) {
        this.nuevoElo = nuevoElo;
    }
}
