package com.padelplay.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Perfil de jugador vinculado a un usuario.
 * Contiene información básica del perfil de jugador.
 */
@Entity
@Table(name = "perfiles_jugador")
public class PerfilJugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(length = 100)
    private String apodo;

    private Integer aniosExperiencia;

    @Column(length = 20)
    private String nivelJuego; // Principiante, Intermedio, Avanzado, Profesional

    @Column(length = 15)
    private String telefono;

    @OneToOne(mappedBy = "perfilJugador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DetallesTecnicos detallesTecnicos;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public PerfilJugador() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public PerfilJugador(Usuario usuario) {
        this();
        this.usuario = usuario;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public Integer getAniosExperiencia() {
        return aniosExperiencia;
    }

    public void setAniosExperiencia(Integer aniosExperiencia) {
        this.aniosExperiencia = aniosExperiencia;
    }

    public String getNivelJuego() {
        return nivelJuego;
    }

    public void setNivelJuego(String nivelJuego) {
        this.nivelJuego = nivelJuego;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public DetallesTecnicos getDetallesTecnicos() {
        return detallesTecnicos;
    }

    public void setDetallesTecnicos(DetallesTecnicos detallesTecnicos) {
        this.detallesTecnicos = detallesTecnicos;
        if (detallesTecnicos != null) {
            detallesTecnicos.setPerfilJugador(this);
        }
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
