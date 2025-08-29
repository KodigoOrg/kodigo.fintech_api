package com.example.finanzas.controller;

import com.example.finanzas.dto.MovimientoDTO;
import com.example.finanzas.entity.*;
import com.example.finanzas.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {
    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public MovimientoController(MovimientoRepository movimientoRepository,
                                UsuarioRepository usuarioRepository,
                                CategoriaRepository categoriaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @PostMapping
    public ResponseEntity<Movimiento> create(@Valid @RequestBody MovimientoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + dto.usuarioId() + " no encontrado"));
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria con ID " + dto.categoriaId() + " no encontrada"));

        Movimiento movimiento = new Movimiento();
        movimiento.setUsuario(usuario);
        movimiento.setCategoria(categoria);
        movimiento.setTipo(dto.tipo());
        movimiento.setMonto(dto.monto());
        movimiento.setDescripcion(dto.descripcion());
        movimiento.setOcurridoEn(dto.ocurridoEn());
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoRepository.save(movimiento));
    }

    @GetMapping
    public ResponseEntity<List<Movimiento>> list() {
        return ResponseEntity.ok(movimientoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> get(@PathVariable UUID id) {
        return movimientoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento con ID " + id + " no encontrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movimiento> update(@PathVariable UUID id, @Valid @RequestBody MovimientoDTO dto) {
        return movimientoRepository.findById(id).map(m -> {
            Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + dto.usuarioId() + " no encontrado"));
            Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria con ID " + dto.categoriaId() + " no encontrada"));
            m.setUsuario(usuario);
            m.setCategoria(categoria);
            m.setTipo(dto.tipo());
            m.setMonto(dto.monto());
            m.setDescripcion(dto.descripcion());
            m.setOcurridoEn(dto.ocurridoEn());
            m.setActualizadoEn(java.time.LocalDateTime.now());
            return ResponseEntity.ok(movimientoRepository.save(m));
        }).orElseThrow(() -> new EntityNotFoundException("Movimiento con ID " + id + " no encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!movimientoRepository.existsById(id)) {
            throw new EntityNotFoundException("Movimiento con ID " + id + " no encontrado");
        }
        movimientoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
