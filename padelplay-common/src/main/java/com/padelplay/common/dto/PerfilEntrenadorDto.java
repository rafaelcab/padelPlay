package com.padelplay.common.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DTO para información del perfil de entrenador.
 */
public class PerfilEntrenadorDto {

    private Long id;
    private String apodo;
    private Integer aniosExperiencia;
    private String telefono;
    private String descripcion;
    private String metodologia;
    private String ubicacion;
    private String clubActual;
    private Boolean disponibleClasesParticulares;
    private Boolean disponibleClasesGrupo;
    private Double precioHoraParticular;
    private Double precioHoraGrupo;
    private String dispLunes;
    private String dispMartes;
    private String dispMiercoles;
    private String dispJueves;
    private String dispViernes;
    private String dispSabado;
    private String dispDomingo;
    private Set<String> especialidades = new HashSet<>();
    private List<CertificacionDto> certificaciones = new ArrayList<>();

    // Campos añadidos para la vista
    private String nombre;
    private String fotoUrl;
    private Double rating;
    private String nivel;
    private String especialidad;
    private Integer experiencia;

    public PerfilEntrenadorDto() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<String> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(Set<String> especialidades) {
        this.especialidades = especialidades;
    }

    public String getDispLunes() {
        return dispLunes;
    }

    public void setDispLunes(String dispLunes) {
        this.dispLunes = dispLunes;
    }

    public String getDispMartes() {
        return dispMartes;
    }

    public void setDispMartes(String dispMartes) {
        this.dispMartes = dispMartes;
    }

    public String getDispMiercoles() {
        return dispMiercoles;
    }

    public void setDispMiercoles(String dispMiercoles) {
        this.dispMiercoles = dispMiercoles;
    }

    public String getDispJueves() {
        return dispJueves;
    }

    public void setDispJueves(String dispJueves) {
        this.dispJueves = dispJueves;
    }

    public String getDispViernes() {
        return dispViernes;
    }

    public void setDispViernes(String dispViernes) {
        this.dispViernes = dispViernes;
    }

    public String getDispSabado() {
        return dispSabado;
    }

    public void setDispSabado(String dispSabado) {
        this.dispSabado = dispSabado;
    }

    public String getDispDomingo() {
        return dispDomingo;
    }

    public void setDispDomingo(String dispDomingo) {
        this.dispDomingo = dispDomingo;
    }

    public List<CertificacionDto> getCertificaciones() {
        return certificaciones;
    }

    public void setCertificaciones(List<CertificacionDto> certificaciones) {
        this.certificaciones = certificaciones;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Integer getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(Integer experiencia) {
        this.experiencia = experiencia;
    }
}
