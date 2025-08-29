package com.example.finanzas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Entity
@Table(name = "categoria", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nombre", "tipo"})
})
public class Categoria {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Size(max = 80)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMov tipo;

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoMov getTipo() { return tipo; }
    public void setTipo(TipoMov tipo) { this.tipo = tipo; }
}
