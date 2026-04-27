package com.padelplay.common.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class ReporteExperienciaDto {

    private Long id;
    private Long partidoId;
    private Long reportanteId;
    private Long reportadoId;
    private Integer valoracion;
    private Set<String> motivos;
    private String comentario;
    private LocalDateTime fechaCreacion;

    public ReporteExperienciaDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public Long getReportanteId() {
        return reportanteId;
    }

    public void setReportanteId(Long reportanteId) {
        this.reportanteId = reportanteId;
    }

    public Long getReportadoId() {
        return reportadoId;
    }

    public void setReportadoId(Long reportadoId) {
        this.reportadoId = reportadoId;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public Set<String> getMotivos() {
        return motivos;
    }

    public void setMotivos(Set<String> motivos) {
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
}
