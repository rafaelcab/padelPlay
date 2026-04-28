package com.padelplay.server.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "resultados_partido",
        indexes = {
                @jakarta.persistence.Index(name = "idx_resultado_partido_id", columnList = "partido_id"),
                @jakarta.persistence.Index(name = "idx_resultado_equipo_a_1", columnList = "equipo_a_jugador_1_id"),
                @jakarta.persistence.Index(name = "idx_resultado_equipo_a_2", columnList = "equipo_a_jugador_2_id"),
                @jakarta.persistence.Index(name = "idx_resultado_equipo_b_1", columnList = "equipo_b_jugador_1_id"),
                @jakarta.persistence.Index(name = "idx_resultado_equipo_b_2", columnList = "equipo_b_jugador_2_id")
        }
)
public class ResultadoPartido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false, unique = true)
    private Partido partido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id", nullable = false)
    private PerfilJugador registradoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_a_jugador_1_id", nullable = false)
    private PerfilJugador equipoAJugador1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_a_jugador_2_id", nullable = false)
    private PerfilJugador equipoAJugador2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_b_jugador_1_id", nullable = false)
    private PerfilJugador equipoBJugador1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_b_jugador_2_id", nullable = false)
    private PerfilJugador equipoBJugador2;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_finalizacion", nullable = false, length = 40)
    private TipoFinalizacionResultadoPartido tipoFinalizacion;

    @Column(name = "juegos_equipo_a")
    private Integer juegosEquipoA;

    @Column(name = "juegos_equipo_b")
    private Integer juegosEquipoB;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_validacion", nullable = false, length = 40)
    private EstadoValidacionResultadoPartido estadoValidacion = EstadoValidacionResultadoPartido.PENDIENTE_VALIDACION;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "resultado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValidacionResultadoPartido> validaciones = new ArrayList<>();

    public ResultadoPartido() {
    }

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
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

    public PerfilJugador getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(PerfilJugador registradoPor) {
        this.registradoPor = registradoPor;
    }

    public PerfilJugador getEquipoAJugador1() {
        return equipoAJugador1;
    }

    public void setEquipoAJugador1(PerfilJugador equipoAJugador1) {
        this.equipoAJugador1 = equipoAJugador1;
    }

    public PerfilJugador getEquipoAJugador2() {
        return equipoAJugador2;
    }

    public void setEquipoAJugador2(PerfilJugador equipoAJugador2) {
        this.equipoAJugador2 = equipoAJugador2;
    }

    public PerfilJugador getEquipoBJugador1() {
        return equipoBJugador1;
    }

    public void setEquipoBJugador1(PerfilJugador equipoBJugador1) {
        this.equipoBJugador1 = equipoBJugador1;
    }

    public PerfilJugador getEquipoBJugador2() {
        return equipoBJugador2;
    }

    public void setEquipoBJugador2(PerfilJugador equipoBJugador2) {
        this.equipoBJugador2 = equipoBJugador2;
    }

    public TipoFinalizacionResultadoPartido getTipoFinalizacion() {
        return tipoFinalizacion;
    }

    public void setTipoFinalizacion(TipoFinalizacionResultadoPartido tipoFinalizacion) {
        this.tipoFinalizacion = tipoFinalizacion;
    }

    public Integer getJuegosEquipoA() {
        return juegosEquipoA;
    }

    public void setJuegosEquipoA(Integer juegosEquipoA) {
        this.juegosEquipoA = juegosEquipoA;
    }

    public Integer getJuegosEquipoB() {
        return juegosEquipoB;
    }

    public void setJuegosEquipoB(Integer juegosEquipoB) {
        this.juegosEquipoB = juegosEquipoB;
    }

    public EstadoValidacionResultadoPartido getEstadoValidacion() {
        return estadoValidacion;
    }

    public void setEstadoValidacion(EstadoValidacionResultadoPartido estadoValidacion) {
        this.estadoValidacion = estadoValidacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<ValidacionResultadoPartido> getValidaciones() {
        return validaciones;
    }

    public void setValidaciones(List<ValidacionResultadoPartido> validaciones) {
        this.validaciones = validaciones;
    }
}
