package com.padelplay.common.dto;

/**
 * DTO con información del estado de perfiles del usuario.
 */
public class EstadoPerfilDto {

    private Long usuarioId;
    private String email;
    private String nombre;
    private String pictureUrl;
    private String rolActivo; // JUGADOR, ENTRENADOR o null
    private boolean tienePerfilJugador;
    private boolean tienePerfilEntrenador;
    private boolean requiereSeleccionPerfil;
    private PerfilJugadorDto perfilJugador;
    private PerfilEntrenadorDto perfilEntrenador;

    public EstadoPerfilDto() {
    }

    // Getters y Setters

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getRolActivo() {
        return rolActivo;
    }

    public void setRolActivo(String rolActivo) {
        this.rolActivo = rolActivo;
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

    public boolean isRequiereSeleccionPerfil() {
        return requiereSeleccionPerfil;
    }

    public void setRequiereSeleccionPerfil(boolean requiereSeleccionPerfil) {
        this.requiereSeleccionPerfil = requiereSeleccionPerfil;
    }

    public PerfilJugadorDto getPerfilJugador() {
        return perfilJugador;
    }

    public void setPerfilJugador(PerfilJugadorDto perfilJugador) {
        this.perfilJugador = perfilJugador;
    }

    public PerfilEntrenadorDto getPerfilEntrenador() {
        return perfilEntrenador;
    }

    public void setPerfilEntrenador(PerfilEntrenadorDto perfilEntrenador) {
        this.perfilEntrenador = perfilEntrenador;
    }
}
