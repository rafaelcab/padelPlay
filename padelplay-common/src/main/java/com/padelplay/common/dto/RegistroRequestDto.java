package com.padelplay.common.dto;

/**
 * DTO para el registro manual de usuarios (email/password).
 */
public class RegistroRequestDto {

    private String nombre;
    private String email;
    private String password;

    public RegistroRequestDto() {
    }

    public RegistroRequestDto(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
