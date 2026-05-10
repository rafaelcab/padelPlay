package com.padelplay.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Solicitud de evaluacion en pista realizada por un jugador a un entrenador.
 */
@Entity
@Table(name = "solicitudes_evaluacion")
public class SolicitudEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long jugadorId;

    @Column(nullable = false)
    private Long entrenadorId;

    @Column(nullable = false)
    private LocalDateTime fechaHoraSolicitada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitudEvaluacion estado;

    @Column(length = 1000)
    private String comentarioJugador;

    @Column(length = 1000)
    private String comentarioEntrenador;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaRespuesta;

    public SolicitudEvaluacion() {
        this.estado = EstadoSolicitudEvaluacion.PENDIENTE;
    }

    public SolicitudEvaluacion(Long jugadorId, Long entrenadorId, LocalDateTime fechaHoraSolicitada) {
        this();
        this.jugadorId = jugadorId;
        this.entrenadorId = entrenadorId;
        this.fechaHoraSolicitada = fechaHoraSolicitada;
    }

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoSolicitudEvaluacion.PENDIENTE;
        }
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

    public LocalDateTime getFechaHoraSolicitada() {
        return fechaHoraSolicitada;
    }

    public void setFechaHoraSolicitada(LocalDateTime fechaHoraSolicitada) {
        this.fechaHoraSolicitada = fechaHoraSolicitada;
    }

    public EstadoSolicitudEvaluacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitudEvaluacion estado) {
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
}
