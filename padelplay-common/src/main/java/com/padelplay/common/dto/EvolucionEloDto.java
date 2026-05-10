package com.padelplay.common.dto;

import java.time.LocalDateTime;

/**
 * DTO para representar un punto de la evolucion del ELO de un jugador.
 */
public class EvolucionEloDto {

    private LocalDateTime fecha;
    private Integer elo;

    public EvolucionEloDto() {
    }

    public EvolucionEloDto(LocalDateTime fecha, Integer elo) {
        this.fecha = fecha;
        this.elo = elo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getElo() {
        return elo;
    }

    public void setElo(Integer elo) {
        this.elo = elo;
    }
}
