package com.padelplay.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PartidoDto {

    private Long id;
    private Long idCreador;
    private LocalDateTime fechaHora;
    private String ubicacion;
    private String tipoPartido;
    private Double nivelRequerido;
    private Integer huecosDisponibles;
    private String codigoAcceso;
    private List<Long> idsJugadoresApuntados;

    public PartidoDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCreador() {
        return idCreador;
    }

    public void setIdCreador(Long idCreador) {
        this.idCreador = idCreador;
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

    public Double getNivelRequerido() {
        return nivelRequerido;
    }

    public void setNivelRequerido(Double nivelRequerido) {
        this.nivelRequerido = nivelRequerido;
    }

    public Integer getHuecosDisponibles() {
        return huecosDisponibles;
    }

    public void setHuecosDisponibles(Integer huecosDisponibles) {
        this.huecosDisponibles = huecosDisponibles;
    }

    public String getCodigoAcceso() {
        return codigoAcceso;
    }

    public void setCodigoAcceso(String codigoAcceso) {
        this.codigoAcceso = codigoAcceso;
    }

    public List<Long> getIdsJugadoresApuntados() {
        return idsJugadoresApuntados;
    }

    public void setIdsJugadoresApuntados(List<Long> idsJugadoresApuntados) {
        this.idsJugadoresApuntados = idsJugadoresApuntados;
    }
}