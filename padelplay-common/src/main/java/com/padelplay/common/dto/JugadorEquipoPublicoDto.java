package com.padelplay.common.dto;

public class JugadorEquipoPublicoDto {

    private Long perfilJugadorId;
    private String apodo;
    private boolean usuarioObjetivo;

    public JugadorEquipoPublicoDto() {
    }

    public Long getPerfilJugadorId() {
        return perfilJugadorId;
    }

    public void setPerfilJugadorId(Long perfilJugadorId) {
        this.perfilJugadorId = perfilJugadorId;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public boolean isUsuarioObjetivo() {
        return usuarioObjetivo;
    }

    public void setUsuarioObjetivo(boolean usuarioObjetivo) {
        this.usuarioObjetivo = usuarioObjetivo;
    }
}
