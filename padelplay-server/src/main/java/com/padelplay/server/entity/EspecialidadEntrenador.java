package com.padelplay.server.entity;

/**
 * Especialidades de entrenamiento que puede ofrecer un entrenador.
 */
public enum EspecialidadEntrenador {
    
    INICIACION("Iniciación", "Clases para principiantes que empiezan de cero"),
    PERFECCIONAMIENTO("Perfeccionamiento", "Mejora de técnica para jugadores con base"),
    COMPETICION("Competición", "Preparación para torneos y competiciones"),
    ALTO_RENDIMIENTO("Alto Rendimiento", "Entrenamiento de élite y profesional"),
    INFANTIL("Infantil", "Clases especializadas para niños y adolescentes"),
    ADULTOS("Adultos", "Clases adaptadas para adultos"),
    TERCERA_EDAD("Tercera Edad", "Clases adaptadas para personas mayores"),
    PADEL_ADAPTADO("Pádel Adaptado", "Clases para personas con diversidad funcional"),
    TACTICA("Táctica", "Especialización en estrategia y táctica de juego"),
    PREPARACION_FISICA("Preparación Física", "Acondicionamiento físico específico para pádel");

    private final String nombre;
    private final String descripcion;

    EspecialidadEntrenador(String nombre, String descripcion) {
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
