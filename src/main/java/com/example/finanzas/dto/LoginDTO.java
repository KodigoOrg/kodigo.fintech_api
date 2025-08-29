package com.example.finanzas.dto;

import jakarta.validation.constraints.*;

public record LoginDTO(
        @Email @NotNull String email,
        @NotNull @Size(min = 6) String password
) {}
