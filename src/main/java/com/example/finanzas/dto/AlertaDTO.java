package com.example.finanzas.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record AlertaDTO(
        @NotNull UUID presupuestoId,
        @NotNull @DecimalMin("0.0") BigDecimal sobrepeso,
        String mensaje,
        Boolean atendido
) {}
