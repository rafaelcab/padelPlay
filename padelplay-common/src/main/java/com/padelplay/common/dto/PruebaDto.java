package com.padelplay.common.dto;

public class PruebaDto {

    private String mensaje;

    public PruebaDto() {
    }

    public PruebaDto(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}