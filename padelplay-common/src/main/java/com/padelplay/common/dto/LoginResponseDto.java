package com.padelplay.common.dto;

public record LoginResponseDto(
        String token,
        Long id,
        String nombre,
        String email
) {}
