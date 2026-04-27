package com.padelplay.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ResultadoPartidoPendienteValidacionDto {

    private Long partidoId;
    private LocalDateTime fechaHora;
    private String ubicacion;
    private String tipoPartido;
    private String tipoFinalizacion;
    private Integer juegosEquipoA;
    private Integer juegosEquipoB;
    private String registradoPorApodo;
    private String estadoValidacion;
    private List<PerfilJugadorDto> equipoA;
    private List<PerfilJugadorDto> equipoB;

    public ResultadoPartidoPendienteValidacionDto() {
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

    public String getRegistradoPorApodo() {
        return registradoPorApodo;
    }

    public void setRegistradoPorApodo(String registradoPorApodo) {
        this.registradoPorApodo = registradoPorApodo;
    }

    public String getEstadoValidacion() {
        return estadoValidacion;
    }

    public void setEstadoValidacion(String estadoValidacion) {
        this.estadoValidacion = estadoValidacion;
    }

    public List<PerfilJugadorDto> getEquipoA() {
        return equipoA;
    }

    public void setEquipoA(List<PerfilJugadorDto> equipoA) {
        this.equipoA = equipoA;
    }

    public List<PerfilJugadorDto> getEquipoB() {
        return equipoB;
    }

    public void setEquipoB(List<PerfilJugadorDto> equipoB) {
        this.equipoB = equipoB;
    }
}
