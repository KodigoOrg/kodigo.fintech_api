package com.example.finanzas.controller;

import com.example.finanzas.dto.UsuarioDTO;
import com.example.finanzas.dto.UsuarioUpdateDTO;
import com.example.finanzas.entity.Usuario;
import com.example.finanzas.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Usuario.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            )
    })
    public ResponseEntity<Usuario> create(@Valid @RequestBody UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setNombre(dto.nombre());
        usuario.setPassword(dto.password());
        usuario.setRole(Usuario.Role.USER);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(usuario));
    }

    @GetMapping
    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene la lista de todos los usuarios registrados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Usuario.class))
            )
    })
    public ResponseEntity<List<Usuario>> list() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Obtiene la información de un usuario específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = Usuario.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    public ResponseEntity<Usuario> get(@PathVariable UUID id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + id + " no encontrado"));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza la información de un usuario existente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = Usuario.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    public ResponseEntity<Usuario> update(@PathVariable UUID id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        return usuarioRepository.findById(id).map(u -> {
            u.setEmail(dto.email());
            u.setNombre(dto.nombre());
            return ResponseEntity.ok(usuarioRepository.save(u));
        }).orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + id + " no encontrado"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario con ID " + id + " no encontrado");
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
