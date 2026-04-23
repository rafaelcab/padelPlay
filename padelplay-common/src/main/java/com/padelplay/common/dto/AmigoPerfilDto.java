package com.padelplay.common.dto;

public class AmigoPerfilDto {

    private Long usuarioId;
    private String nombre;
    private String email;
    private String pictureUrl;
    private boolean tienePerfilJugador;
    private boolean tienePerfilEntrenador;

    private String jugadorApodo;
    private Integer jugadorAniosExperiencia;
    private String jugadorNivelJuego;
    private String jugadorTelefono;
    private Double jugadorNivel;
    private String jugadorPosicion;
    private String jugadorEstiloJuego;
    private String jugadorManoHabil;
    private String jugadorObservaciones;

    private String entrenadorApodo;
    private Integer entrenadorAniosExperiencia;
    private String entrenadorTelefono;
    private String entrenadorDescripcion;
    private String entrenadorMetodologia;
    private String entrenadorUbicacion;
    private String entrenadorClubActual;
    private String entrenadorDispLunes;
    private String entrenadorDispMartes;
    private String entrenadorDispMiercoles;
    private String entrenadorDispJueves;
    private String entrenadorDispViernes;
    private String entrenadorDispSabado;
    private String entrenadorDispDomingo;

    private boolean seguido;
    private Long totalSeguidores;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public boolean isTienePerfilJugador() {
        return tienePerfilJugador;
    }

    public void setTienePerfilJugador(boolean tienePerfilJugador) {
        this.tienePerfilJugador = tienePerfilJugador;
    }

    public boolean isTienePerfilEntrenador() {
        return tienePerfilEntrenador;
    }

    public void setTienePerfilEntrenador(boolean tienePerfilEntrenador) {
        this.tienePerfilEntrenador = tienePerfilEntrenador;
    }

    public String getJugadorApodo() {
        return jugadorApodo;
    }

    public void setJugadorApodo(String jugadorApodo) {
        this.jugadorApodo = jugadorApodo;
    }

    public Integer getJugadorAniosExperiencia() {
        return jugadorAniosExperiencia;
    }

    public void setJugadorAniosExperiencia(Integer jugadorAniosExperiencia) {
        this.jugadorAniosExperiencia = jugadorAniosExperiencia;
    }

    public String getJugadorNivelJuego() {
        return jugadorNivelJuego;
    }

    public void setJugadorNivelJuego(String jugadorNivelJuego) {
        this.jugadorNivelJuego = jugadorNivelJuego;
    }

    public String getJugadorTelefono() {
        return jugadorTelefono;
    }

    public void setJugadorTelefono(String jugadorTelefono) {
        this.jugadorTelefono = jugadorTelefono;
    }

    public Double getJugadorNivel() {
        return jugadorNivel;
    }

    public void setJugadorNivel(Double jugadorNivel) {
        this.jugadorNivel = jugadorNivel;
    }

    public String getJugadorPosicion() {
        return jugadorPosicion;
    }

    public void setJugadorPosicion(String jugadorPosicion) {
        this.jugadorPosicion = jugadorPosicion;
    }

    public String getJugadorEstiloJuego() {
        return jugadorEstiloJuego;
    }

    public void setJugadorEstiloJuego(String jugadorEstiloJuego) {
        this.jugadorEstiloJuego = jugadorEstiloJuego;
    }

    public String getJugadorManoHabil() {
        return jugadorManoHabil;
    }

    public void setJugadorManoHabil(String jugadorManoHabil) {
        this.jugadorManoHabil = jugadorManoHabil;
    }

    public String getJugadorObservaciones() {
        return jugadorObservaciones;
    }

    public void setJugadorObservaciones(String jugadorObservaciones) {
        this.jugadorObservaciones = jugadorObservaciones;
    }

    public String getEntrenadorApodo() {
        return entrenadorApodo;
    }

    public void setEntrenadorApodo(String entrenadorApodo) {
        this.entrenadorApodo = entrenadorApodo;
    }

    public Integer getEntrenadorAniosExperiencia() {
        return entrenadorAniosExperiencia;
    }

    public void setEntrenadorAniosExperiencia(Integer entrenadorAniosExperiencia) {
        this.entrenadorAniosExperiencia = entrenadorAniosExperiencia;
    }

    public String getEntrenadorTelefono() {
        return entrenadorTelefono;
    }

    public void setEntrenadorTelefono(String entrenadorTelefono) {
        this.entrenadorTelefono = entrenadorTelefono;
    }

    public String getEntrenadorDescripcion() {
        return entrenadorDescripcion;
    }

    public void setEntrenadorDescripcion(String entrenadorDescripcion) {
        this.entrenadorDescripcion = entrenadorDescripcion;
    }

    public String getEntrenadorMetodologia() {
        return entrenadorMetodologia;
    }

    public void setEntrenadorMetodologia(String entrenadorMetodologia) {
        this.entrenadorMetodologia = entrenadorMetodologia;
    }

    public String getEntrenadorUbicacion() {
        return entrenadorUbicacion;
    }

    public void setEntrenadorUbicacion(String entrenadorUbicacion) {
        this.entrenadorUbicacion = entrenadorUbicacion;
    }

    public String getEntrenadorClubActual() {
        return entrenadorClubActual;
    }

    public void setEntrenadorClubActual(String entrenadorClubActual) {
        this.entrenadorClubActual = entrenadorClubActual;
    }

    public String getEntrenadorDispLunes() {
        return entrenadorDispLunes;
    }

    public void setEntrenadorDispLunes(String entrenadorDispLunes) {
        this.entrenadorDispLunes = entrenadorDispLunes;
    }

    public String getEntrenadorDispMartes() {
        return entrenadorDispMartes;
    }

    public void setEntrenadorDispMartes(String entrenadorDispMartes) {
        this.entrenadorDispMartes = entrenadorDispMartes;
    }

    public String getEntrenadorDispMiercoles() {
        return entrenadorDispMiercoles;
    }

    public void setEntrenadorDispMiercoles(String entrenadorDispMiercoles) {
        this.entrenadorDispMiercoles = entrenadorDispMiercoles;
    }

    public String getEntrenadorDispJueves() {
        return entrenadorDispJueves;
    }

    public void setEntrenadorDispJueves(String entrenadorDispJueves) {
        this.entrenadorDispJueves = entrenadorDispJueves;
    }

    public String getEntrenadorDispViernes() {
        return entrenadorDispViernes;
    }

    public void setEntrenadorDispViernes(String entrenadorDispViernes) {
        this.entrenadorDispViernes = entrenadorDispViernes;
    }

    public String getEntrenadorDispSabado() {
        return entrenadorDispSabado;
    }

    public void setEntrenadorDispSabado(String entrenadorDispSabado) {
        this.entrenadorDispSabado = entrenadorDispSabado;
    }

    public String getEntrenadorDispDomingo() {
        return entrenadorDispDomingo;
    }

    public void setEntrenadorDispDomingo(String entrenadorDispDomingo) {
        this.entrenadorDispDomingo = entrenadorDispDomingo;
    }

    public boolean isSeguido() {
        return seguido;
    }

    public void setSeguido(boolean seguido) {
        this.seguido = seguido;
    }

    public Long getTotalSeguidores() {
        return totalSeguidores;
    }

    public void setTotalSeguidores(Long totalSeguidores) {
        this.totalSeguidores = totalSeguidores;
    }
}