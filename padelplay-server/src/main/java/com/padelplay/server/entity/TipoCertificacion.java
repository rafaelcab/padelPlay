package com.padelplay.server.entity;

/**
 * Tipos de certificación para entrenadores de pádel.
 */
public enum TipoCertificacion {
    
    FEDERADO_NACIONAL("Federado Nacional", "Certificación oficial de la Federación Española de Pádel"),
    FEDERADO_AUTONOMICO("Federado Autonómico", "Certificación de federación autonómica"),
    MONITOR_TIEMPO_LIBRE("Monitor de Tiempo Libre", "Titulación de monitor de tiempo libre"),
    INSTRUCTOR_PADEL("Instructor de Pádel", "Titulación de instructor de pádel"),
    ENTRENADOR_NIVEL_1("Entrenador Nivel 1", "Primer nivel de entrenador federado"),
    ENTRENADOR_NIVEL_2("Entrenador Nivel 2", "Segundo nivel de entrenador federado"),
    ENTRENADOR_NIVEL_3("Entrenador Nivel 3", "Tercer nivel de entrenador federado (máximo)"),
    MASTER_PADEL("Máster en Pádel", "Titulación de máster universitario o equivalente"),
    CERTIFICACION_INTERNACIONAL("Certificación Internacional", "Certificación de organismo internacional"),
    OTRO("Otro", "Otra certificación o titulación");

    private final String nombre;
    private final String descripcion;

    TipoCertificacion(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
