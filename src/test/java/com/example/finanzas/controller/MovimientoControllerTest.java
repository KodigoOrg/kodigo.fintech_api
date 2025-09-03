package com.example.finanzas.controller;

import com.example.finanzas.dto.MovimientoDTO;
import com.example.finanzas.entity.Categoria;
import com.example.finanzas.entity.Movimiento;
import com.example.finanzas.entity.TipoMov;
import com.example.finanzas.entity.Usuario;
import com.example.finanzas.repository.CategoriaRepository;
import com.example.finanzas.repository.MovimientoRepository;
import com.example.finanzas.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MovimientoControllerTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private MovimientoController movimientoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private MovimientoDTO movimientoDTO;
    private Movimiento movimiento;
    private Usuario usuario;
    private Categoria categoria;
    private UUID usuarioId;
    private UUID categoriaId;
    private UUID movimientoId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movimientoController).build();
        objectMapper = new ObjectMapper();

        usuarioId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();
        movimientoId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setNombre("Test User");

        categoria = new Categoria();
        categoria.setNombre("Comida");

        movimientoDTO = new MovimientoDTO(
                usuarioId,
                categoriaId,
                TipoMov.EGRESO,
                new BigDecimal("50.00"),
                "Almuerzo",
                LocalDate.now()
        );

        movimiento = new Movimiento();
        movimiento.setUsuario(usuario);
        movimiento.setCategoria(categoria);
        movimiento.setTipo(TipoMov.EGRESO);
        movimiento.setMonto(new BigDecimal("50.00"));
        movimiento.setDescripcion("Almuerzo");
        movimiento.setOcurridoEn(LocalDate.now());
    }

    @Test
    void create_ShouldCreateMovimientoSuccessfully() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);

        // Act
        ResponseEntity<Movimiento> response = movimientoController.create(movimientoDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(movimiento, response.getBody());

        verify(usuarioRepository).findById(usuarioId);
        verify(categoriaRepository).findById(categoriaId);
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void create_ShouldThrowExceptionWhenUsuarioNotFound() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            movimientoController.create(movimientoDTO)
        );

        verify(usuarioRepository).findById(usuarioId);
        verify(categoriaRepository, never()).findById(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowExceptionWhenCategoriaNotFound() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            movimientoController.create(movimientoDTO)
        );

        verify(usuarioRepository).findById(usuarioId);
        verify(categoriaRepository).findById(categoriaId);
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void list_ShouldReturnAllMovimientos() {
        // Arrange
        List<Movimiento> movimientos = List.of(movimiento);
        when(movimientoRepository.findAll()).thenReturn(movimientos);

        // Act
        ResponseEntity<List<Movimiento>> response = movimientoController.list();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movimientos, response.getBody());
        assertEquals(1, response.getBody().size());

        verify(movimientoRepository).findAll();
    }

    @Test
    void get_ShouldReturnMovimientoById() {
        // Arrange
        when(movimientoRepository.findById(movimientoId)).thenReturn(Optional.of(movimiento));

        // Act
        ResponseEntity<Movimiento> response = movimientoController.get(movimientoId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movimiento, response.getBody());

        verify(movimientoRepository).findById(movimientoId);
    }

    @Test
    void get_ShouldThrowExceptionWhenMovimientoNotFound() {
        // Arrange
        when(movimientoRepository.findById(movimientoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            movimientoController.get(movimientoId)
        );

        verify(movimientoRepository).findById(movimientoId);
    }

    @Test
    void update_ShouldUpdateMovimientoSuccessfully() {
        // Arrange
        when(movimientoRepository.findById(movimientoId)).thenReturn(Optional.of(movimiento));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);

        // Act
        ResponseEntity<Movimiento> response = movimientoController.update(movimientoId, movimientoDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(movimientoRepository).findById(movimientoId);
        verify(usuarioRepository).findById(usuarioId);
        verify(categoriaRepository).findById(categoriaId);
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenMovimientoNotFound() {
        // Arrange
        when(movimientoRepository.findById(movimientoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            movimientoController.update(movimientoId, movimientoDTO)
        );

        verify(movimientoRepository).findById(movimientoId);
        verify(usuarioRepository, never()).findById(any());
        verify(categoriaRepository, never()).findById(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteMovimientoSuccessfully() {
        // Arrange
        when(movimientoRepository.existsById(movimientoId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = movimientoController.delete(movimientoId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(movimientoRepository).existsById(movimientoId);
        verify(movimientoRepository).deleteById(movimientoId);
    }

    @Test
    void delete_ShouldThrowExceptionWhenMovimientoNotFound() {
        // Arrange
        when(movimientoRepository.existsById(movimientoId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            movimientoController.delete(movimientoId)
        );

        verify(movimientoRepository).existsById(movimientoId);
        verify(movimientoRepository, never()).deleteById(any());
    }

    @Test
    void create_ShouldSetCorrectMovimientoProperties() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);

        // Act
        movimientoController.create(movimientoDTO);

        // Assert
        verify(movimientoRepository).save(argThat(mov -> 
            mov.getUsuario().equals(usuario) &&
            mov.getCategoria().equals(categoria) &&
            mov.getTipo().equals(movimientoDTO.tipo()) &&
            mov.getMonto().equals(movimientoDTO.monto()) &&
            mov.getDescripcion().equals(movimientoDTO.descripcion()) &&
            mov.getOcurridoEn().equals(movimientoDTO.ocurridoEn())
        ));
    }
}
