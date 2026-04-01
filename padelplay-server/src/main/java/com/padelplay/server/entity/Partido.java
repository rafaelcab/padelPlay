package com.padelplay.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "partidos")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private String ubicacion;

    @Column(name = "tipo_partido", nullable = false)
    private String tipoPartido;

    @Column(name = "nivel_requerido", nullable = false)
    private Double nivelRequerido;

    @Column(name = "huecos_disponibles", nullable = false)
    private Integer huecosDisponibles;

    @Column(name = "codigo_acceso")
    private String codigoAcceso;

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private PerfilJugador creador;

    @ManyToMany
    @JoinTable(
        name = "partido_jugadores",
        joinColumns = @JoinColumn(name = "partido_id"),
        inverseJoinColumns = @JoinColumn(name = "perfil_jugador_id")
    )
    private List<PerfilJugador> jugadoresApuntados;

    public Partido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipoPartido() {
        return tipoPartido;
    }

    public void setTipoPartido(String tipoPartido) {
        this.tipoPartido = tipoPartido;
    }

    public Double getNivelRequerido() {
        return nivelRequerido;
    }

    public void setNivelRequerido(Double nivelRequerido) {
        this.nivelRequerido = nivelRequerido;
    }

    public Integer getHuecosDisponibles() {
        return huecosDisponibles;
    }

    public void setHuecosDisponibles(Integer huecosDisponibles) {
        this.huecosDisponibles = huecosDisponibles;
    }

    public String getCodigoAcceso() {
        return codigoAcceso;
    }

    public void setCodigoAcceso(String codigoAcceso) {
        this.codigoAcceso = codigoAcceso;
    }

    public PerfilJugador getCreador() {
        return creador;
    }

    public void setCreador(PerfilJugador creador) {
        this.creador = creador;
    }

    public List<PerfilJugador> getJugadoresApuntados() {
        return jugadoresApuntados;
    }

    public void setJugadoresApuntados(List<PerfilJugador> jugadoresApuntados) {
        this.jugadoresApuntados = jugadoresApuntados;
    }
}