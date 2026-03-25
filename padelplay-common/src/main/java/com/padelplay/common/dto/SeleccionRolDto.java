package com.padelplay.common.dto;

/**
 * DTO para la selección inicial de rol.
 */
public class SeleccionRolDto {

    private String rol; // JUGADOR o ENTRENADOR

    public SeleccionRolDto() {
    }

    public SeleccionRolDto(String rol) {
        this.rol = rol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
