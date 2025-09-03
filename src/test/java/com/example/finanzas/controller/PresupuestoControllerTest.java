package com.example.finanzas.controller;

import com.example.finanzas.dto.PresupuestoDTO;
import com.example.finanzas.entity.Categoria;
import com.example.finanzas.entity.Presupuesto;
import com.example.finanzas.entity.TipoMov;
import com.example.finanzas.entity.Usuario;
import com.example.finanzas.repository.CategoriaRepository;
import com.example.finanzas.repository.PresupuestoRepository;
import com.example.finanzas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresupuestoControllerTest {

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private PresupuestoController presupuestoController;

    private PresupuestoDTO presupuestoDTO;
    private Presupuesto presupuesto;
    private Usuario usuario;
    private Categoria categoria;
    private UUID presupuestoId;
    private UUID usuarioId;
    private UUID categoriaId;

    @BeforeEach
    void setUp() {
        presupuestoId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setNombre("Test User");

        categoria = new Categoria();
        categoria.setNombre("Comida");
        categoria.setTipo(TipoMov.EGRESO);

        presupuestoDTO = new PresupuestoDTO(
                usuarioId,
                categoriaId,
                "2024-01",
                new BigDecimal("500.00")
        );

        presupuesto = new Presupuesto();
        presupuesto.setUsuario(usuario);
        presupuesto.setCategoria(categoria);
        presupuesto.setPeriodo("2024-01");
        presupuesto.setMonto(new BigDecimal("500.00"));
    }

    @Test
    void create_ShouldCreatePresupuestoSuccessfully() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(presupuestoRepository.save(any(Presupuesto.class))).thenReturn(presupuesto);

        // Act
        ResponseEntity<Presupuesto> response = presupuestoController.create(presupuestoDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(presupuesto, response.getBody());

        verify(usuarioRepository).findById(usuarioId);
        verify(categoriaRepository).findById(categoriaId);
        verify(presupuestoRepository).save(any(Presupuesto.class));
    }

    @Test
    void create_ShouldThrowExceptionWhenUsuarioNotFound() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                presupuestoController.create(presupuestoDTO)
        );

        verify(usuarioRepository).findById(usuarioId);
        verify(categoriaRepository, never()).findById(any());
        verify(presupuestoRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowExceptionWhenCategoriaNotFound() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                presupuestoController.create(presupuestoDTO)
        );

        verify(usuarioRepository).findById(usuarioId);
        verify(categoriaRepository).findById(categoriaId);
        verify(presupuestoRepository, never()).save(any());
    }

    @Test
    void list_ShouldReturnAllPresupuestos() {
        // Arrange
        List<Presupuesto> presupuestos = List.of(presupuesto);
        when(presupuestoRepository.findAll()).thenReturn(presupuestos);

        // Act
        ResponseEntity<List<Presupuesto>> response = presupuestoController.list();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(presupuestos, response.getBody());
        assertEquals(1, response.getBody().size());

        verify(presupuestoRepository).findAll();
    }

    @Test
    void get_ShouldReturnPresupuestoById() {
        // Arrange
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.of(presupuesto));

        // Act
        ResponseEntity<Presupuesto> response = presupuestoController.get(presupuestoId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(presupuesto, response.getBody());

        verify(presupuestoRepository).findById(presupuestoId);
    }

    @Test
    void get_ShouldThrowExceptionWhenPresupuestoNotFound() {
        // Arrange
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                presupuestoController.get(presupuestoId)
        );

        verify(presupuestoRepository).findById(presupuestoId);
    }

    @Test
    void update_ShouldUpdatePresupuestoSuccessfully() {
        // Arrange
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.of(presupuesto));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(presupuestoRepository.save(any(Presupuesto.class))).thenReturn(presupuesto);

        // Act
        ResponseEntity<Presupuesto> response = presupuestoController.update(presupuestoId, presupuestoDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(presupuestoRepository).findById(presupuestoId);
        verify(usuarioRepository).findById(usuarioId);
        verify(categoriaRepository).findById(categoriaId);
        verify(presupuestoRepository).save(any(Presupuesto.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenPresupuestoNotFound() {
        // Arrange
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                presupuestoController.update(presupuestoId, presupuestoDTO)
        );

        verify(presupuestoRepository).findById(presupuestoId);
        verify(usuarioRepository, never()).findById(any());
        verify(categoriaRepository, never()).findById(any());
        verify(presupuestoRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeletePresupuestoSuccessfully() {
        // Arrange
        when(presupuestoRepository.existsById(presupuestoId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = presupuestoController.delete(presupuestoId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(presupuestoRepository).existsById(presupuestoId);
        verify(presupuestoRepository).deleteById(presupuestoId);
    }

    @Test
    void delete_ShouldThrowExceptionWhenPresupuestoNotFound() {
        // Arrange
        when(presupuestoRepository.existsById(presupuestoId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                presupuestoController.delete(presupuestoId)
        );

        verify(presupuestoRepository).existsById(presupuestoId);
        verify(presupuestoRepository, never()).deleteById(any());
    }

    @Test
    void create_ShouldSetCorrectPresupuestoProperties() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(presupuestoRepository.save(any(Presupuesto.class))).thenReturn(presupuesto);

        // Act
        presupuestoController.create(presupuestoDTO);

        // Assert
        verify(presupuestoRepository).save(argThat(p ->
            p.getUsuario().equals(usuario) &&
            p.getCategoria().equals(categoria) &&
            p.getPeriodo().equals(presupuestoDTO.periodo()) &&
            p.getMonto().equals(presupuestoDTO.monto())
        ));
    }

    @Test
    void update_ShouldUpdateCorrectPresupuestoProperties() {
        // Arrange
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.of(presupuesto));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(presupuestoRepository.save(any(Presupuesto.class))).thenReturn(presupuesto);

        // Act
        presupuestoController.update(presupuestoId, presupuestoDTO);

        // Assert
        verify(presupuestoRepository).save(argThat(p ->
            p.getUsuario().equals(usuario) &&
            p.getCategoria().equals(categoria) &&
            p.getPeriodo().equals(presupuestoDTO.periodo()) &&
            p.getMonto().equals(presupuestoDTO.monto())
        ));
    }
}
