package com.example.finanzas.dto;

import com.example.finanzas.entity.TipoMov;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MovimientoDTO(
        @NotNull UUID usuarioId,
        @NotNull UUID categoriaId,
        @NotNull TipoMov tipo,
        @NotNull @DecimalMin("0.0") BigDecimal monto,
        String descripcion,
        @NotNull LocalDate ocurridoEn
) {}
