package com.example.finanzas.dto;

public record AuthResponseDTO(
        String token,
        String email,
        String nombre
) {}
