package com.example.finanzas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue
    private UUID id;

    @Email
    @NotNull
    @Size(max = 120)
    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @NotNull
    @Size(max = 120)
    private String nombre;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
