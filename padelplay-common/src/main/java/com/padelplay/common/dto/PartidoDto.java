package com.padelplay.common.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class PartidoDto {

    private Long id;

    // Cambiamos el ID suelto por el DTO completo para mostrar el Apodo/Nivel en la
    // tarjeta
    private PerfilJugadorDto creador;

    private LocalDateTime fechaHora;
    private String ubicacion;
    private String tipoPartido; // "PÚBLICO" o "PRIVADO"
    private Double nivelRequerido;
    private Integer huecosDisponibles;
    private String codigoAcceso;
    private boolean cancelado;
    private boolean terminado;
    private String resultado;
    private Set<Long> confirmacionesResultadoIds;
    private Set<Long> rechazosResultadoIds;

    // Cambiamos la lista de IDs por la lista de perfiles para los iconos/avatares
    private List<PerfilJugadorDto> jugadoresApuntados;

    public PartidoDto() {
    }

    // ==========================================
    // Getters y Setters
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PerfilJugadorDto getCreador() {
        return creador;
    }

    public void setCreador(PerfilJugadorDto creador) {
        this.creador = creador;
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

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Set<Long> getConfirmacionesResultadoIds() {
        return confirmacionesResultadoIds;
    }

    public void setConfirmacionesResultadoIds(Set<Long> confirmacionesResultadoIds) {
        this.confirmacionesResultadoIds = confirmacionesResultadoIds;
    }

    public Set<Long> getRechazosResultadoIds() {
        return rechazosResultadoIds;
    }

    public void setRechazosResultadoIds(Set<Long> rechazosResultadoIds) {
        this.rechazosResultadoIds = rechazosResultadoIds;
    }

    public List<PerfilJugadorDto> getJugadoresApuntados() {
        return jugadoresApuntados;
    }

    public void setJugadoresApuntados(List<PerfilJugadorDto> jugadoresApuntados) {
        this.jugadoresApuntados = jugadoresApuntados;
    }
}
