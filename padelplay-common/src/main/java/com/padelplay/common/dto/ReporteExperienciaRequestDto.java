package com.padelplay.common.dto;

import java.util.Set;

public class ReporteExperienciaRequestDto {

    private Long reportadoId;
    private Integer valoracion;
    private Set<String> motivos;
    private String comentario;

    public ReporteExperienciaRequestDto() {
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
}
