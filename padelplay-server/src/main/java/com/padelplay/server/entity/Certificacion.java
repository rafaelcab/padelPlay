package com.padelplay.server.entity;

import jakarta.persistence.*;

/**
 * Certificación profesional de un entrenador de pádel.
 */
@Entity
@Table(name = "certificaciones")
public class Certificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_entrenador_id", nullable = false)
    private PerfilEntrenador perfilEntrenador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCertificacion tipoCertificacion;

    @Column(length = 100)
    private String nivel;

    @Column(length = 200)
    private String organismo;

    private Integer anioObtencion;

    @Column(length = 50)
    private String numeroRegistro;

    @Column(nullable = false)
    private Boolean verificada = false;

    public Certificacion() {
    }

    public Certificacion(TipoCertificacion tipoCertificacion) {
        this.tipoCertificacion = tipoCertificacion;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PerfilEntrenador getPerfilEntrenador() {
        return perfilEntrenador;
    }

    public void setPerfilEntrenador(PerfilEntrenador perfilEntrenador) {
        this.perfilEntrenador = perfilEntrenador;
    }

    public TipoCertificacion getTipoCertificacion() {
        return tipoCertificacion;
    }

    public void setTipoCertificacion(TipoCertificacion tipoCertificacion) {
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
