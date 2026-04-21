package com.padelplay.server.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "reportes_experiencia_partido",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reporte_experiencia_partido",
                columnNames = {"partido_id", "reportante_id", "reportado_id"}
        )
)
public class ReporteExperienciaPartido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reportante_id", nullable = false)
    private PerfilJugador reportante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reportado_id", nullable = false)
    private PerfilJugador reportado;

    @Column(nullable = false)
    private Integer valoracion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "reporte_experiencia_motivos",
            joinColumns = @JoinColumn(name = "reporte_id")
    )
    @Column(name = "motivo", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<MotivoReporteExperiencia> motivos = new HashSet<>();

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = this.fechaCreacion;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public PerfilJugador getReportante() {
        return reportante;
    }

    public void setReportante(PerfilJugador reportante) {
        this.reportante = reportante;
    }

    public PerfilJugador getReportado() {
        return reportado;
    }

    public void setReportado(PerfilJugador reportado) {
        this.reportado = reportado;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public Set<MotivoReporteExperiencia> getMotivos() {
        return motivos;
    }

    public void setMotivos(Set<MotivoReporteExperiencia> motivos) {
        this.motivos = motivos;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
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
