package com.padelplay.common.dto;

import java.time.LocalDateTime;

public class FeedbackPartidoDto {
    private Long id;
    private Long alumnoId;
    private String alumnoApodo;
    private Long partidoId;
    private LocalDateTime fechaPartido;
    private String ubicacionPartido;
    private Double calificacion;
    private String comentario;
    private String fortalezas;
    private String areasMejora;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public FeedbackPartidoDto() {
    }

    public FeedbackPartidoDto(Long id, Long alumnoId, String alumnoApodo, Long partidoId, 
                               LocalDateTime fechaPartido, String ubicacionPartido, Double calificacion, 
                               String comentario, String fortalezas, String areasMejora, 
                               LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.alumnoId = alumnoId;
        this.alumnoApodo = alumnoApodo;
        this.partidoId = partidoId;
        this.fechaPartido = fechaPartido;
        this.ubicacionPartido = ubicacionPartido;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fortalezas = fortalezas;
        this.areasMejora = areasMejora;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getAlumnoApodo() {
        return alumnoApodo;
    }

    public void setAlumnoApodo(String alumnoApodo) {
        this.alumnoApodo = alumnoApodo;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public LocalDateTime getFechaPartido() {
        return fechaPartido;
    }

    public void setFechaPartido(LocalDateTime fechaPartido) {
        this.fechaPartido = fechaPartido;
    }

    public String getUbicacionPartido() {
        return ubicacionPartido;
    }

    public void setUbicacionPartido(String ubicacionPartido) {
        this.ubicacionPartido = ubicacionPartido;
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

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
