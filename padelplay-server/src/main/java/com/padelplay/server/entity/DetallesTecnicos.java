package com.padelplay.server.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Detalles técnico-tácticos del perfil de jugador.
 * Contiene información sobre posición, estilo y golpes fuertes.
 */
@Entity
@Table(name = "detalles_tecnicos")
public class DetallesTecnicos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_jugador_id", nullable = false, unique = true)
    private PerfilJugador perfilJugador;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Posicion posicion;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstiloJuego estiloJuego;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "jugador_golpes_fuertes",
        joinColumns = @JoinColumn(name = "detalles_tecnicos_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "golpe", length = 20)
    private Set<TipoGolpe> golpesFuertes = new HashSet<>();

    @Column(length = 500)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private ManoHabil manoHabil;

    public DetallesTecnicos() {
    }

    public DetallesTecnicos(PerfilJugador perfilJugador) {
        this.perfilJugador = perfilJugador;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PerfilJugador getPerfilJugador() {
        return perfilJugador;
    }

    public void setPerfilJugador(PerfilJugador perfilJugador) {
        this.perfilJugador = perfilJugador;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public EstiloJuego getEstiloJuego() {
        return estiloJuego;
    }

    public void setEstiloJuego(EstiloJuego estiloJuego) {
        this.estiloJuego = estiloJuego;
    }

    public Set<TipoGolpe> getGolpesFuertes() {
        return golpesFuertes;
    }

    public void setGolpesFuertes(Set<TipoGolpe> golpesFuertes) {
        this.golpesFuertes = golpesFuertes;
    }

    public void addGolpeFuerte(TipoGolpe golpe) {
        this.golpesFuertes.add(golpe);
    }

    public void removeGolpeFuerte(TipoGolpe golpe) {
        this.golpesFuertes.remove(golpe);
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public ManoHabil getManoHabil() {
        return manoHabil;
    }

    public void setManoHabil(ManoHabil manoHabil) {
        this.manoHabil = manoHabil;
    }

    /**
     * Mano hábil del jugador.
     */
    public enum ManoHabil {
        DIESTRO("Diestro"),
        ZURDO("Zurdo"),
        AMBIDIESTRO("Ambidiestro");

        private final String descripcion;

        ManoHabil(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
}
