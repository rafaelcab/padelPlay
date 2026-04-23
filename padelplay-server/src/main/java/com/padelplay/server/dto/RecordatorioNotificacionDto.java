package com.padelplay.server.dto;

import java.time.LocalDateTime;

public record RecordatorioNotificacionDto(
        Long id,
        Long partidoId,
        String titulo,
        String mensaje,
        String ubicacion,
        String tipoPartido,
        LocalDateTime fechaHora,
        LocalDateTime programadoPara,
        String estado
) {
}