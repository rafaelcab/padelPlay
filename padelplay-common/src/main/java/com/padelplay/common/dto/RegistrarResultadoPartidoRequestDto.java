package com.padelplay.common.dto;

import java.util.List;

public class RegistrarResultadoPartidoRequestDto {

    private List<Long> equipoAJugadorIds;
    private List<Long> equipoBJugadorIds;
    private String tipoFinalizacion;
    private Integer juegosEquipoA;
    private Integer juegosEquipoB;

    public RegistrarResultadoPartidoRequestDto() {
    }

    public List<Long> getEquipoAJugadorIds() {
        return equipoAJugadorIds;
    }

    public void setEquipoAJugadorIds(List<Long> equipoAJugadorIds) {
        this.equipoAJugadorIds = equipoAJugadorIds;
    }

    public List<Long> getEquipoBJugadorIds() {
        return equipoBJugadorIds;
    }

    public void setEquipoBJugadorIds(List<Long> equipoBJugadorIds) {
        this.equipoBJugadorIds = equipoBJugadorIds;
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
}
