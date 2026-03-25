package com.padelplay.common.dto;

/**
 * DTO de respuesta tras autenticación exitosa.
 */
public class AuthResponseDto {

    private String token;
    private String email;
    private String nombre;
    private String pictureUrl;
    private boolean nuevoUsuario;

    public AuthResponseDto() {
    }

    public AuthResponseDto(String token, String email, String nombre, String pictureUrl, boolean nuevoUsuario) {
        this.token = token;
        this.email = email;
        this.nombre = nombre;
        this.pictureUrl = pictureUrl;
        this.nuevoUsuario = nuevoUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public boolean isNuevoUsuario() {
        return nuevoUsuario;
    }

    public void setNuevoUsuario(boolean nuevoUsuario) {
        this.nuevoUsuario = nuevoUsuario;
    }
}
