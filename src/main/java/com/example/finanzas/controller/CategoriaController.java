package com.example.finanzas.controller;

import com.example.finanzas.dto.CategoriaDTO;
import com.example.finanzas.entity.Categoria;
import com.example.finanzas.repository.CategoriaRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @PostMapping
    public ResponseEntity<Categoria> create(@Valid @RequestBody CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre());
        categoria.setTipo(dto.tipo());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaRepository.save(categoria));
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> list() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> get(@PathVariable UUID id) {
        return categoriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Categoria con ID " + id + " no encontrada"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable UUID id, @Valid @RequestBody CategoriaDTO dto) {
        return categoriaRepository.findById(id).map(c -> {
            c.setNombre(dto.nombre());
            c.setTipo(dto.tipo());
            return ResponseEntity.ok(categoriaRepository.save(c));
        }).orElseThrow(() -> new EntityNotFoundException("Categoria con ID " + id + " no encontrada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!categoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoria con ID " + id + " no encontrada");
        }
        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
