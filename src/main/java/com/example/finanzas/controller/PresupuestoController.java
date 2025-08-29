package com.example.finanzas.controller;

import com.example.finanzas.dto.PresupuestoDTO;
import com.example.finanzas.entity.*;
import com.example.finanzas.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoController {
    private final PresupuestoRepository presupuestoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public PresupuestoController(PresupuestoRepository presupuestoRepository,
                                 UsuarioRepository usuarioRepository,
                                 CategoriaRepository categoriaRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @PostMapping
    public ResponseEntity<Presupuesto> create(@Valid @RequestBody PresupuestoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + dto.usuarioId() + " no encontrado"));
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria con ID " + dto.categoriaId() + " no encontrada"));

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setUsuario(usuario);
        presupuesto.setCategoria(categoria);
        presupuesto.setPeriodo(dto.periodo());
        presupuesto.setMonto(dto.monto());
        return ResponseEntity.status(HttpStatus.CREATED).body(presupuestoRepository.save(presupuesto));
    }

    @GetMapping
    public ResponseEntity<List<Presupuesto>> list() {
        return ResponseEntity.ok(presupuestoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presupuesto> get(@PathVariable UUID id) {
        return presupuestoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Presupuesto con ID " + id + " no encontrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Presupuesto> update(@PathVariable UUID id, @Valid @RequestBody PresupuestoDTO dto) {
        return presupuestoRepository.findById(id).map(p -> {
            Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + dto.usuarioId() + " no encontrado"));
            Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria con ID " + dto.categoriaId() + " no encontrada"));
            p.setUsuario(usuario);
            p.setCategoria(categoria);
            p.setPeriodo(dto.periodo());
            p.setMonto(dto.monto());
            return ResponseEntity.ok(presupuestoRepository.save(p));
        }).orElseThrow(() -> new EntityNotFoundException("Presupuesto con ID " + id + " no encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!presupuestoRepository.existsById(id)) {
            throw new EntityNotFoundException("Presupuesto con ID " + id + " no encontrado");
        }
        presupuestoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
