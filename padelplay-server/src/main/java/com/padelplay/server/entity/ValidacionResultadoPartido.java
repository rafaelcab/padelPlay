package com.padelplay.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "validaciones_resultado_partido",
        uniqueConstraints = @UniqueConstraint(columnNames = {"resultado_id", "validador_id"})
)
public class ValidacionResultadoPartido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resultado_id", nullable = false)
    private ResultadoPartido resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validador_id", nullable = false)
    private PerfilJugador validador;

    @Column(nullable = false)
    private boolean aceptado;

    @Column(name = "fecha_validacion", nullable = false, updatable = false)
    private LocalDateTime fechaValidacion;

    public ValidacionResultadoPartido() {
    }

    @PrePersist
    protected void onCreate() {
        this.fechaValidacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResultadoPartido getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoPartido resultado) {
        this.resultado = resultado;
    }

    public PerfilJugador getValidador() {
        return validador;
    }

    public void setValidador(PerfilJugador validador) {
        this.validador = validador;
    }

    public boolean isAceptado() {
        return aceptado;
    }

    public void setAceptado(boolean aceptado) {
        this.aceptado = aceptado;
    }

    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }
}
