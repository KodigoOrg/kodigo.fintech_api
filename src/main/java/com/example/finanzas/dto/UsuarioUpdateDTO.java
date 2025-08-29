package com.example.finanzas.dto;

import jakarta.validation.constraints.*;

public record UsuarioUpdateDTO(
        @Email @NotNull @Size(max = 120) String email,
        @NotNull @Size(max = 120) String nombre
) {}
