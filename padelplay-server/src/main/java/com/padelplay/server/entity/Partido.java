package com.padelplay.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(nullable = false)
    private boolean cancelado = false;

    @Column(nullable = false)
    private boolean terminado = false;

    @Column
    private String resultado;

    @ElementCollection
    private Set<Long> confirmacionesResultadoIds = new HashSet<>();

    @ElementCollection
    private Set<Long> rechazosResultadoIds = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private PerfilJugador creador;

    @ManyToMany
    @JoinTable(name = "partido_jugadores", joinColumns = @JoinColumn(name = "partido_id"), inverseJoinColumns = @JoinColumn(name = "perfil_jugador_id"))
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

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Set<Long> getConfirmacionesResultadoIds() {
        return confirmacionesResultadoIds;
    }

    public void setConfirmacionesResultadoIds(Set<Long> confirmacionesResultadoIds) {
        this.confirmacionesResultadoIds = confirmacionesResultadoIds;
    }

    public Set<Long> getRechazosResultadoIds() {
        return rechazosResultadoIds;
    }

    public void setRechazosResultadoIds(Set<Long> rechazosResultadoIds) {
        this.rechazosResultadoIds = rechazosResultadoIds;
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

    // Método de ayuda para gestionar la inscripción
    public void añadirJugador(PerfilJugador jugador) {
        if (this.huecosDisponibles > 0) {
            this.jugadoresApuntados.add(jugador);
            this.huecosDisponibles--;
        }
    }
}
