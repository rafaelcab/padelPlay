package com.padelplay.common.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PartidoJugadoPublicoDto {

    private Long partidoId;
    private LocalDateTime fechaHora;
    private String ubicacion;
    private String tipoPartido;
    private String tipoFinalizacion;
    private Integer juegosEquipoA;
    private Integer juegosEquipoB;
    private List<JugadorEquipoPublicoDto> equipoA = new ArrayList<>();
    private List<JugadorEquipoPublicoDto> equipoB = new ArrayList<>();
    private String equipoUsuarioObjetivo;
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

    public String getTipoFinalizacion() {
        return tipoFinalizacion;
    }

    public void setTipoFinalizacion(String tipoFinalizacion) {
        this.tipoFinalizacion = tipoFinalizacion;
    }

    public Integer getJuegosEquipoA() {
        return juegosEquipoA;
    }

    public void setJuegosEquipoA(Integer juegosEquipoA) {
        this.juegosEquipoA = juegosEquipoA;
    }

    public Integer getJuegosEquipoB() {
        return juegosEquipoB;
    }

    public void setJuegosEquipoB(Integer juegosEquipoB) {
        this.juegosEquipoB = juegosEquipoB;
    }

    public List<JugadorEquipoPublicoDto> getEquipoA() {
        return equipoA;
    }

    public void setEquipoA(List<JugadorEquipoPublicoDto> equipoA) {
        this.equipoA = equipoA;
    }

    public List<JugadorEquipoPublicoDto> getEquipoB() {
        return equipoB;
    }

    public void setEquipoB(List<JugadorEquipoPublicoDto> equipoB) {
        this.equipoB = equipoB;
    }

    public String getEquipoUsuarioObjetivo() {
        return equipoUsuarioObjetivo;
    }

    public void setEquipoUsuarioObjetivo(String equipoUsuarioObjetivo) {
        this.equipoUsuarioObjetivo = equipoUsuarioObjetivo;
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
