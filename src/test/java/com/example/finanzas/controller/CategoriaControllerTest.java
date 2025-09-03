package com.example.finanzas.controller;

import com.example.finanzas.dto.CategoriaDTO;
import com.example.finanzas.entity.Categoria;
import com.example.finanzas.entity.TipoMov;
import com.example.finanzas.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaController categoriaController;

    private CategoriaDTO categoriaDTO;
    private Categoria categoria;
    private UUID categoriaId;

    @BeforeEach
    void setUp() {
        categoriaId = UUID.randomUUID();
        
        categoriaDTO = new CategoriaDTO("Comida", TipoMov.EGRESO);
        
        categoria = new Categoria();
        categoria.setNombre("Comida");
        categoria.setTipo(TipoMov.EGRESO);
    }

    @Test
    void create_ShouldCreateCategoriaSuccessfully() {
        // Arrange
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // Act
        ResponseEntity<Categoria> response = categoriaController.create(categoriaDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(categoria, response.getBody());

        verify(categoriaRepository).save(argThat(c -> 
            c.getNombre().equals(categoriaDTO.nombre()) &&
            c.getTipo().equals(categoriaDTO.tipo())
        ));
    }

    @Test
    void create_ShouldSetCorrectCategoriaProperties() {
        // Arrange
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // Act
        categoriaController.create(categoriaDTO);

        // Assert
        verify(categoriaRepository).save(argThat(c -> 
            c.getNombre().equals("Comida") &&
            c.getTipo() == TipoMov.EGRESO
        ));
    }

    @Test
    void list_ShouldReturnAllCategorias() {
        // Arrange
        List<Categoria> categorias = List.of(categoria);
        when(categoriaRepository.findAll()).thenReturn(categorias);

        // Act
        ResponseEntity<List<Categoria>> response = categoriaController.list();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categorias, response.getBody());
        assertEquals(1, response.getBody().size());

        verify(categoriaRepository).findAll();
    }

    @Test
    void get_ShouldReturnCategoriaById() {
        // Arrange
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));

        // Act
        ResponseEntity<Categoria> response = categoriaController.get(categoriaId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoria, response.getBody());

        verify(categoriaRepository).findById(categoriaId);
    }

    @Test
    void get_ShouldThrowExceptionWhenCategoriaNotFound() {
        // Arrange
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                categoriaController.get(categoriaId)
        );

        assertEquals("Categoria con ID " + categoriaId + " no encontrada", exception.getMessage());
        verify(categoriaRepository).findById(categoriaId);
    }

    @Test
    void update_ShouldUpdateCategoriaSuccessfully() {
        // Arrange
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // Act
        ResponseEntity<Categoria> response = categoriaController.update(categoriaId, categoriaDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(categoriaRepository).findById(categoriaId);
        verify(categoriaRepository).save(argThat(c -> 
            c.getNombre().equals(categoriaDTO.nombre()) &&
            c.getTipo().equals(categoriaDTO.tipo())
        ));
    }

    @Test
    void update_ShouldThrowExceptionWhenCategoriaNotFound() {
        // Arrange
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                categoriaController.update(categoriaId, categoriaDTO)
        );

        assertEquals("Categoria con ID " + categoriaId + " no encontrada", exception.getMessage());
        verify(categoriaRepository).findById(categoriaId);
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteCategoriaSuccessfully() {
        // Arrange
        when(categoriaRepository.existsById(categoriaId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = categoriaController.delete(categoriaId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(categoriaRepository).existsById(categoriaId);
        verify(categoriaRepository).deleteById(categoriaId);
    }

    @Test
    void delete_ShouldThrowExceptionWhenCategoriaNotFound() {
        // Arrange
        when(categoriaRepository.existsById(categoriaId)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                categoriaController.delete(categoriaId)
        );

        assertEquals("Categoria con ID " + categoriaId + " no encontrada", exception.getMessage());
        verify(categoriaRepository).existsById(categoriaId);
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void create_ShouldWorkWithIngresoType() {
        // Arrange
        CategoriaDTO ingresoDTO = new CategoriaDTO("Salario", TipoMov.INGRESO);
        Categoria ingresoCategoria = new Categoria();
        ingresoCategoria.setNombre("Salario");
        ingresoCategoria.setTipo(TipoMov.INGRESO);
        
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(ingresoCategoria);

        // Act
        ResponseEntity<Categoria> response = categoriaController.create(ingresoDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TipoMov.INGRESO, response.getBody().getTipo());

        verify(categoriaRepository).save(argThat(c -> c.getTipo() == TipoMov.INGRESO));
    }

    @Test
    void update_ShouldPreserveExistingCategoriaId() {
        // Arrange
        Categoria existingCategoria = new Categoria();
        existingCategoria.setNombre("Old Name");
        existingCategoria.setTipo(TipoMov.EGRESO);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(existingCategoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(existingCategoria);

        // Act
        categoriaController.update(categoriaId, categoriaDTO);

        // Assert
        verify(categoriaRepository).save(argThat(c -> 
            c.getNombre().equals(categoriaDTO.nombre()) &&
            c.getTipo().equals(categoriaDTO.tipo())
        ));
    }
}
