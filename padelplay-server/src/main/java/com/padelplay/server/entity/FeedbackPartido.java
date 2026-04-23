package com.padelplay.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Feedback de un entrenador sobre el rendimiento de un alumno en un partido.
 */
@Entity
@Table(name = "feedback_partidos")
public class FeedbackPartido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenador_id", nullable = false)
    private PerfilEntrenador entrenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    private PerfilJugador alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @Column(nullable = false)
    private Double calificacion; // 1.0 a 5.0

    @Column(length = 2000)
    private String comentario;

    @Column(length = 500)
    private String fortalezas; // Puntos fuertes del alumno

    @Column(length = 500)
    private String areasMejora; // Áreas a mejorar

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public FeedbackPartido() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public FeedbackPartido(PerfilEntrenador entrenador, PerfilJugador alumno, Partido partido) {
        this();
        this.entrenador = entrenador;
        this.alumno = alumno;
        this.partido = partido;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PerfilEntrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(PerfilEntrenador entrenador) {
        this.entrenador = entrenador;
    }

    public PerfilJugador getAlumno() {
        return alumno;
    }

    public void setAlumno(PerfilJugador alumno) {
        this.alumno = alumno;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFortalezas() {
        return fortalezas;
    }

    public void setFortalezas(String fortalezas) {
        this.fortalezas = fortalezas;
    }

    public String getAreasMejora() {
        return areasMejora;
    }

    public void setAreasMejora(String areasMejora) {
        this.areasMejora = areasMejora;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
