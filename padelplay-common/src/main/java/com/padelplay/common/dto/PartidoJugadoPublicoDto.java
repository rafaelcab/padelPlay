package com.padelplay.common.dto;

import java.time.LocalDateTime;

public class PartidoJugadoPublicoDto {

    private Long partidoId;
    private LocalDateTime fechaHora;
    private String ubicacion;
    private String tipoPartido;
    private Long creadorId;
    private String creadorApodo;
    private boolean usuarioObjetivoFueCreador;

    public PartidoJugadoPublicoDto() {
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipoPartido() {
        return tipoPartido;
    }

    public void setTipoPartido(String tipoPartido) {
        this.tipoPartido = tipoPartido;
    }

    public Long getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Long creadorId) {
        this.creadorId = creadorId;
    }

    public String getCreadorApodo() {
        return creadorApodo;
    }

    public void setCreadorApodo(String creadorApodo) {
        this.creadorApodo = creadorApodo;
    }

    public boolean isUsuarioObjetivoFueCreador() {
        return usuarioObjetivoFueCreador;
    }

    public void setUsuarioObjetivoFueCreador(boolean usuarioObjetivoFueCreador) {
        this.usuarioObjetivoFueCreador = usuarioObjetivoFueCreador;
    }
}
