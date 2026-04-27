package com.padelplay.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ResultadoPartidoDto {

    private Long partidoId;
    private String tipoFinalizacion;
    private Integer juegosEquipoA;
    private Integer juegosEquipoB;
    private String estadoValidacion;
    private LocalDateTime fechaRegistro;
    private Long registradoPorPerfilJugadorId;
    private String registradoPorApodo;
    private boolean partidoTerminado;
    private Integer validacionesAprobadas;
    private Integer validacionesRechazadas;
    private Integer validacionesPendientes;
    private List<PerfilJugadorDto> equipoA;
    private List<PerfilJugadorDto> equipoB;
    private List<ValidacionResultadoPartidoDto> validaciones;

    public ResultadoPartidoDto() {
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
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

    public String getEstadoValidacion() {
        return estadoValidacion;
    }

    public void setEstadoValidacion(String estadoValidacion) {
        this.estadoValidacion = estadoValidacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Long getRegistradoPorPerfilJugadorId() {
        return registradoPorPerfilJugadorId;
    }

    public void setRegistradoPorPerfilJugadorId(Long registradoPorPerfilJugadorId) {
        this.registradoPorPerfilJugadorId = registradoPorPerfilJugadorId;
    }

    public String getRegistradoPorApodo() {
        return registradoPorApodo;
    }

    public void setRegistradoPorApodo(String registradoPorApodo) {
        this.registradoPorApodo = registradoPorApodo;
    }

    public boolean isPartidoTerminado() {
        return partidoTerminado;
    }

    public void setPartidoTerminado(boolean partidoTerminado) {
        this.partidoTerminado = partidoTerminado;
    }

    public Integer getValidacionesAprobadas() {
        return validacionesAprobadas;
    }

    public void setValidacionesAprobadas(Integer validacionesAprobadas) {
        this.validacionesAprobadas = validacionesAprobadas;
    }

    public Integer getValidacionesRechazadas() {
        return validacionesRechazadas;
    }

    public void setValidacionesRechazadas(Integer validacionesRechazadas) {
        this.validacionesRechazadas = validacionesRechazadas;
    }

    public Integer getValidacionesPendientes() {
        return validacionesPendientes;
    }

    public void setValidacionesPendientes(Integer validacionesPendientes) {
        this.validacionesPendientes = validacionesPendientes;
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

    public List<ValidacionResultadoPartidoDto> getValidaciones() {
        return validaciones;
    }

    public void setValidaciones(List<ValidacionResultadoPartidoDto> validaciones) {
        this.validaciones = validaciones;
    }
}
