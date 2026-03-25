package com.padelplay.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Perfil de entrenador vinculado a un usuario.
 * Contiene información profesional del entrenador de pádel.
 */
@Entity
@Table(name = "perfiles_entrenador")
public class PerfilEntrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(length = 100)
    private String apodo;

    private Integer aniosExperiencia;

    @Column(length = 15)
    private String telefono;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 500)
    private String metodologia;

    @Column(length = 200)
    private String ubicacion;

    @Column(length = 200)
    private String clubActual;

    @Column(nullable = false)
    private Boolean disponibleClasesParticulares = true;

    @Column(nullable = false)
    private Boolean disponibleClasesGrupo = true;

    private Double precioHoraParticular;

    private Double precioHoraGrupo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "entrenador_especialidades", joinColumns = @JoinColumn(name = "perfil_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "especialidad")
    private Set<EspecialidadEntrenador> especialidades = new HashSet<>();

    @OneToMany(mappedBy = "perfilEntrenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificacion> certificaciones = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public PerfilEntrenador() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public PerfilEntrenador(Usuario usuario) {
        this();
        this.usuario = usuario;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Métodos de utilidad

    public void agregarCertificacion(Certificacion certificacion) {
        certificaciones.add(certificacion);
        certificacion.setPerfilEntrenador(this);
    }

    public void eliminarCertificacion(Certificacion certificacion) {
        certificaciones.remove(certificacion);
        certificacion.setPerfilEntrenador(null);
    }

    public void agregarEspecialidad(EspecialidadEntrenador especialidad) {
        especialidades.add(especialidad);
    }

    public void eliminarEspecialidad(EspecialidadEntrenador especialidad) {
        especialidades.remove(especialidad);
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(String metodologia) {
        this.metodologia = metodologia;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getClubActual() {
        return clubActual;
    }

    public void setClubActual(String clubActual) {
        this.clubActual = clubActual;
    }

    public Boolean getDisponibleClasesParticulares() {
        return disponibleClasesParticulares;
    }

    public void setDisponibleClasesParticulares(Boolean disponibleClasesParticulares) {
        this.disponibleClasesParticulares = disponibleClasesParticulares;
    }

    public Boolean getDisponibleClasesGrupo() {
        return disponibleClasesGrupo;
    }

    public void setDisponibleClasesGrupo(Boolean disponibleClasesGrupo) {
        this.disponibleClasesGrupo = disponibleClasesGrupo;
    }

    public Double getPrecioHoraParticular() {
        return precioHoraParticular;
    }

    public void setPrecioHoraParticular(Double precioHoraParticular) {
        this.precioHoraParticular = precioHoraParticular;
    }

    public Double getPrecioHoraGrupo() {
        return precioHoraGrupo;
    }

    public void setPrecioHoraGrupo(Double precioHoraGrupo) {
        this.precioHoraGrupo = precioHoraGrupo;
    }

    public Set<EspecialidadEntrenador> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(Set<EspecialidadEntrenador> especialidades) {
        this.especialidades = especialidades;
    }

    public List<Certificacion> getCertificaciones() {
        return certificaciones;
    }

    public void setCertificaciones(List<Certificacion> certificaciones) {
        this.certificaciones = certificaciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
