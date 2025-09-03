package com.example.finanzas.controller;

import com.example.finanzas.dto.AlertaDTO;
import com.example.finanzas.entity.Alerta;
import com.example.finanzas.entity.Categoria;
import com.example.finanzas.entity.Presupuesto;
import com.example.finanzas.entity.TipoMov;
import com.example.finanzas.entity.Usuario;
import com.example.finanzas.repository.AlertaRepository;
import com.example.finanzas.repository.PresupuestoRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaControllerTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @InjectMocks
    private AlertaController alertaController;

    private AlertaDTO alertaDTO;
    private Alerta alerta;
    private Presupuesto presupuesto;
    private Usuario usuario;
    private Categoria categoria;
    private UUID alertaId;
    private UUID presupuestoId;

    @BeforeEach
    void setUp() {
        alertaId = UUID.randomUUID();
        presupuestoId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setNombre("Test User");

        categoria = new Categoria();
        categoria.setNombre("Comida");
        categoria.setTipo(TipoMov.EGRESO);

        presupuesto = new Presupuesto();
        presupuesto.setUsuario(usuario);
        presupuesto.setCategoria(categoria);
        presupuesto.setPeriodo("2024-01");
        presupuesto.setMonto(new BigDecimal("500.00"));

        alertaDTO = new AlertaDTO(
                presupuestoId,
                new BigDecimal("600.00"),
                "Presupuesto excedido",
                false
        );

        alerta = new Alerta();
        alerta.setPresupuesto(presupuesto);
        alerta.setSobrepeso(new BigDecimal("600.00"));
        alerta.setMensaje("Presupuesto excedido");
        alerta.setAtendido(false);
    }

    @Test
    void create_ShouldCreateAlertaSuccessfully() {
        // Arrange
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.of(presupuesto));
        when(alertaRepository.save(any(Alerta.class))).thenReturn(alerta);

        // Act
        ResponseEntity<Alerta> response = alertaController.create(alertaDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(alerta, response.getBody());

        verify(presupuestoRepository).findById(presupuestoId);
        verify(alertaRepository).save(any(Alerta.class));
    }

    @Test
    void create_ShouldThrowExceptionWhenPresupuestoNotFound() {
        // Arrange
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                alertaController.create(alertaDTO)
        );

        verify(presupuestoRepository).findById(presupuestoId);
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void create_ShouldSetDefaultAtendidoValueWhenNull() {
        // Arrange
        AlertaDTO dtoWithNullAtendido = new AlertaDTO(
                presupuestoId,
                new BigDecimal("600.00"),
                "Presupuesto excedido",
                null
        );
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.of(presupuesto));
        when(alertaRepository.save(any(Alerta.class))).thenReturn(alerta);

        // Act
        alertaController.create(dtoWithNullAtendido);

        // Assert
        verify(alertaRepository).save(argThat(a -> !a.getAtendido()));
    }

    @Test
    void list_ShouldReturnAllAlertas() {
        // Arrange
        List<Alerta> alertas = List.of(alerta);
        when(alertaRepository.findAll()).thenReturn(alertas);

        // Act
        ResponseEntity<List<Alerta>> response = alertaController.list();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(alertas, response.getBody());
        assertEquals(1, response.getBody().size());

        verify(alertaRepository).findAll();
    }

    @Test
    void get_ShouldReturnAlertaById() {
        // Arrange
        when(alertaRepository.findById(alertaId)).thenReturn(Optional.of(alerta));

        // Act
        ResponseEntity<Alerta> response = alertaController.get(alertaId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(alerta, response.getBody());

        verify(alertaRepository).findById(alertaId);
    }

    @Test
    void get_ShouldThrowExceptionWhenAlertaNotFound() {
        // Arrange
        when(alertaRepository.findById(alertaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                alertaController.get(alertaId)
        );

        verify(alertaRepository).findById(alertaId);
    }

    @Test
    void update_ShouldUpdateAlertaSuccessfully() {
        // Arrange
        when(alertaRepository.findById(alertaId)).thenReturn(Optional.of(alerta));
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.of(presupuesto));
        when(alertaRepository.save(any(Alerta.class))).thenReturn(alerta);

        // Act
        ResponseEntity<Alerta> response = alertaController.update(alertaId, alertaDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(alertaRepository).findById(alertaId);
        verify(presupuestoRepository).findById(presupuestoId);
        verify(alertaRepository).save(any(Alerta.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenAlertaNotFound() {
        // Arrange
        when(alertaRepository.findById(alertaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                alertaController.update(alertaId, alertaDTO)
        );

        verify(alertaRepository).findById(alertaId);
        verify(presupuestoRepository, never()).findById(any());
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteAlertaSuccessfully() {
        // Arrange
        when(alertaRepository.existsById(alertaId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = alertaController.delete(alertaId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(alertaRepository).existsById(alertaId);
        verify(alertaRepository).deleteById(alertaId);
    }

    @Test
    void delete_ShouldThrowExceptionWhenAlertaNotFound() {
        // Arrange
        when(alertaRepository.existsById(alertaId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                alertaController.delete(alertaId)
        );

        verify(alertaRepository).existsById(alertaId);
        verify(alertaRepository, never()).deleteById(any());
    }

    @Test
    void create_ShouldSetCorrectAlertaProperties() {
        // Arrange
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.of(presupuesto));
        when(alertaRepository.save(any(Alerta.class))).thenReturn(alerta);

        // Act
        alertaController.create(alertaDTO);

        // Assert
        verify(alertaRepository).save(argThat(a ->
            a.getPresupuesto().equals(presupuesto) &&
            a.getSobrepeso().equals(alertaDTO.sobrepeso()) &&
            a.getMensaje().equals(alertaDTO.mensaje()) &&
            a.getAtendido().equals(alertaDTO.atendido())
        ));
    }

    @Test
    void update_ShouldUpdateCorrectAlertaProperties() {
        // Arrange
        when(alertaRepository.findById(alertaId)).thenReturn(Optional.of(alerta));
        when(presupuestoRepository.findById(presupuestoId)).thenReturn(Optional.of(presupuesto));
        when(alertaRepository.save(any(Alerta.class))).thenReturn(alerta);

        // Act
        alertaController.update(alertaId, alertaDTO);

        // Assert
        verify(alertaRepository).save(argThat(a ->
            a.getPresupuesto().equals(presupuesto) &&
            a.getSobrepeso().equals(alertaDTO.sobrepeso()) &&
            a.getMensaje().equals(alertaDTO.mensaje()) &&
            a.getAtendido().equals(alertaDTO.atendido())
        ));
    }
}

