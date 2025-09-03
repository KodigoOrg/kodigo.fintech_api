package com.example.finanzas.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        // Setup común si es necesario
    }

    @Test
    void handleNotFound_ShouldReturnNotFoundStatusAndErrorMessage() {
        // Arrange
        String errorMessage = "Usuario no encontrado";
        EntityNotFoundException exception = new EntityNotFoundException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    void handleValidation_ShouldReturnBadRequestStatusAndFieldErrors() {
        // Arrange
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("email", "Email no puede estar vacío");
        expectedErrors.put("password", "Password debe tener al menos 6 caracteres");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("usuario", "email", "Email no puede estar vacío"),
                new FieldError("usuario", "password", "Password debe tener al menos 6 caracteres")
        ));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null, bindingResult
        );

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidation(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedErrors, response.getBody());
    }

    @Test
    void handleValidation_ShouldReturnEmptyMapWhenNoFieldErrors() {
        // Arrange
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null, bindingResult
        );

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidation(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void handleGeneric_ShouldReturnInternalServerErrorStatusAndGenericErrorMessage() {
        // Arrange
        String errorMessage = "Error de base de datos";
        Exception exception = new RuntimeException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleGeneric(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error interno: " + errorMessage, response.getBody().get("error"));
    }

    @Test
    void handleGeneric_ShouldHandleNullExceptionMessage() {
        // Arrange
        Exception exception = new RuntimeException();

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleGeneric(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error interno: null", response.getBody().get("error"));
    }

    @Test
    void handleNotFound_ShouldHandleExceptionWithEmptyMessage() {
        // Arrange
        EntityNotFoundException exception = new EntityNotFoundException("");

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("", response.getBody().get("error"));
    }

    @Test
    void handleValidation_ShouldHandleSingleFieldError() {
        // Arrange
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("usuario", "nombre", "Nombre es requerido")
        ));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null, bindingResult
        );

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidation(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Nombre es requerido", response.getBody().get("nombre"));
    }
}

