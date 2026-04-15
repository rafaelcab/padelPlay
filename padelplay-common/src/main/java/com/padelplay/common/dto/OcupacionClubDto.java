package com.padelplay.common.dto;

public class OcupacionClubDto {

    private String hora;
    private Double porcentajeOcupacion;

    public OcupacionClubDto() {
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Double getPorcentajeOcupacion() {
        return porcentajeOcupacion;
    }

    public void setPorcentajeOcupacion(Double porcentajeOcupacion) {
        this.porcentajeOcupacion = porcentajeOcupacion;
    }
}
