package com.padelplay.common.dto;

/**
 * DTO para certificación de un entrenador.
 */
public class CertificacionDto {

    private Long id;
    private String tipoCertificacion;
    private String nivel;
    private String organismo;
    private Integer anioObtencion;
    private String numeroRegistro;
    private Boolean verificada;

    public CertificacionDto() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoCertificacion() {
        return tipoCertificacion;
    }

    public void setTipoCertificacion(String tipoCertificacion) {
        this.tipoCertificacion = tipoCertificacion;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getOrganismo() {
        return organismo;
    }

    public void setOrganismo(String organismo) {
        this.organismo = organismo;
    }

    public Integer getAnioObtencion() {
        return anioObtencion;
    }

    public void setAnioObtencion(Integer anioObtencion) {
        this.anioObtencion = anioObtencion;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public Boolean getVerificada() {
        return verificada;
    }

    public void setVerificada(Boolean verificada) {
        this.verificada = verificada;
    }
}
