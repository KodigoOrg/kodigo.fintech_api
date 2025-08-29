package com.example.finanzas.dto;

import com.example.finanzas.entity.TipoMov;
import jakarta.validation.constraints.*;

public record CategoriaDTO(
        @NotNull @Size(max = 80) String nombre,
        @NotNull TipoMov tipo
) {}
