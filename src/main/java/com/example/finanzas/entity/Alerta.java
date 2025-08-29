package com.example.finanzas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerta")
public class Alerta {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "presupuesto_id")
    private Presupuesto presupuesto;

    private LocalDateTime generadoEn = LocalDateTime.now();

    private String mensaje;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal sobrepeso;

    @NotNull
    private Boolean atendido = false;

    public UUID getId() { return id; }
    public Presupuesto getPresupuesto() { return presupuesto; }
    public void setPresupuesto(Presupuesto presupuesto) { this.presupuesto = presupuesto; }
    public LocalDateTime getGeneradoEn() { return generadoEn; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public BigDecimal getSobrepeso() { return sobrepeso; }
    public void setSobrepeso(BigDecimal sobrepeso) { this.sobrepeso = sobrepeso; }
    public Boolean getAtendido() { return atendido; }
    public void setAtendido(Boolean atendido) { this.atendido = atendido; }
}
