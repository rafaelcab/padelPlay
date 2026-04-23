package com.padelplay.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "recordatorios_partido",
    uniqueConstraints = @UniqueConstraint(columnNames = {"partido_id", "destinatario_email"})
)
public class RecordatorioPartido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @Column(name = "destinatario_email", nullable = false)
    private String destinatarioEmail;

    @Column(name = "destinatario_nombre", nullable = false)
    private String destinatarioNombre;

    @Column(name = "programado_para", nullable = false)
    private LocalDateTime programadoPara;

    @Column(name = "enviado_en")
    private LocalDateTime enviadoEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRecordatorio estado = EstadoRecordatorio.PENDIENTE;

    @Column(name = "ultimo_error", length = 1000)
    private String ultimoError;

    public RecordatorioPartido() {
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

    public String getDestinatarioEmail() {
        return destinatarioEmail;
    }

    public void setDestinatarioEmail(String destinatarioEmail) {
        this.destinatarioEmail = destinatarioEmail;
    }

    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }

    public void setDestinatarioNombre(String destinatarioNombre) {
        this.destinatarioNombre = destinatarioNombre;
    }

    public LocalDateTime getProgramadoPara() {
        return programadoPara;
    }

    public void setProgramadoPara(LocalDateTime programadoPara) {
        this.programadoPara = programadoPara;
    }

    public LocalDateTime getEnviadoEn() {
        return enviadoEn;
    }

    public void setEnviadoEn(LocalDateTime enviadoEn) {
        this.enviadoEn = enviadoEn;
    }

    public EstadoRecordatorio getEstado() {
        return estado;
    }

    public void setEstado(EstadoRecordatorio estado) {
        this.estado = estado;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    public void setUltimoError(String ultimoError) {
        this.ultimoError = ultimoError;
    }
}