package com.example.finanzas.controller;

import com.example.finanzas.dto.AlertaDTO;
import com.example.finanzas.entity.*;
import com.example.finanzas.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {
    private final AlertaRepository alertaRepository;
    private final PresupuestoRepository presupuestoRepository;

    public AlertaController(AlertaRepository alertaRepository,
                            PresupuestoRepository presupuestoRepository) {
        this.alertaRepository = alertaRepository;
        this.presupuestoRepository = presupuestoRepository;
    }

    @PostMapping
    public ResponseEntity<Alerta> create(@Valid @RequestBody AlertaDTO dto) {
        Presupuesto presupuesto = presupuestoRepository.findById(dto.presupuestoId())
                .orElseThrow(() -> new EntityNotFoundException("Presupuesto con ID " + dto.presupuestoId() + " no encontrado"));

        Alerta alerta = new Alerta();
        alerta.setPresupuesto(presupuesto);
        alerta.setSobrepeso(dto.sobrepeso());
        alerta.setMensaje(dto.mensaje());
        alerta.setAtendido(dto.atendido() != null ? dto.atendido() : false);
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaRepository.save(alerta));
    }

    @GetMapping
    public ResponseEntity<List<Alerta>> list() {
        return ResponseEntity.ok(alertaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerta> get(@PathVariable UUID id) {
        return alertaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Alerta con ID " + id + " no encontrada"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alerta> update(@PathVariable UUID id, @Valid @RequestBody AlertaDTO dto) {
        return alertaRepository.findById(id).map(a -> {
            Presupuesto presupuesto = presupuestoRepository.findById(dto.presupuestoId())
                    .orElseThrow(() -> new EntityNotFoundException("Presupuesto con ID " + dto.presupuestoId() + " no encontrado"));
            a.setPresupuesto(presupuesto);
            a.setSobrepeso(dto.sobrepeso());
            a.setMensaje(dto.mensaje());
            a.setAtendido(dto.atendido());
            return ResponseEntity.ok(alertaRepository.save(a));
        }).orElseThrow(() -> new EntityNotFoundException("Alerta con ID " + id + " no encontrada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!alertaRepository.existsById(id)) {
            throw new EntityNotFoundException("Alerta con ID " + id + " no encontrada");
        }
        alertaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
