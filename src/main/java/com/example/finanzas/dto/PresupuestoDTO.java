package com.example.finanzas.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record PresupuestoDTO(
        @NotNull UUID usuarioId,
        @NotNull UUID categoriaId,
        @NotNull @Size(min = 7, max = 7) String periodo,
        @NotNull @DecimalMin("0.0") BigDecimal monto
) {}
