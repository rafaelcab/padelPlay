package com.padelplay.common.dto;

import java.time.LocalDateTime;

/**
 * DTO para representar un punto de la evolución ELO de un jugador.
 */
public class EvolucionEloDto {

    private LocalDateTime fecha;
    private Double elo;

    public EvolucionEloDto() {
    }

    public EvolucionEloDto(LocalDateTime fecha, Double elo) {
        this.fecha = fecha;
        this.elo = elo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Double getElo() {
        return elo;
    }

    public void setElo(Double elo) {
        this.elo = elo;
    }
}
