package com.example.finanzas.controller;

import com.example.finanzas.dto.UsuarioDTO;
import com.example.finanzas.dto.UsuarioUpdateDTO;
import com.example.finanzas.entity.Usuario;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO usuarioDTO;
    private UsuarioUpdateDTO usuarioUpdateDTO;
    private Usuario usuario;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        
        usuarioDTO = new UsuarioDTO("test@example.com", "Test User", "password123");
        usuarioUpdateDTO = new UsuarioUpdateDTO("updated@example.com", "Updated User");
        
        usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setNombre("Test User");
        usuario.setPassword("password123");
        usuario.setRole(Usuario.Role.USER);
    }

    @Test
    void create_ShouldCreateUsuarioSuccessfully() {
        // Arrange
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        ResponseEntity<Usuario> response = usuarioController.create(usuarioDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuario, response.getBody());

        verify(usuarioRepository).save(argThat(u -> 
            u.getEmail().equals(usuarioDTO.email()) &&
            u.getNombre().equals(usuarioDTO.nombre()) &&
            u.getPassword().equals(usuarioDTO.password()) &&
            u.getRole() == Usuario.Role.USER
        ));
    }

    @Test
    void create_ShouldSetDefaultRoleAsUser() {
        // Arrange
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioController.create(usuarioDTO);

        // Assert
        verify(usuarioRepository).save(argThat(u -> u.getRole() == Usuario.Role.USER));
    }

    @Test
    void list_ShouldReturnAllUsuarios() {
        // Arrange
        List<Usuario> usuarios = List.of(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        ResponseEntity<List<Usuario>> response = usuarioController.list();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarios, response.getBody());
        assertEquals(1, response.getBody().size());

        verify(usuarioRepository).findAll();
    }

    @Test
    void get_ShouldReturnUsuarioById() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // Act
        ResponseEntity<Usuario> response = usuarioController.get(usuarioId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario, response.getBody());

        verify(usuarioRepository).findById(usuarioId);
    }

    @Test
    void get_ShouldThrowExceptionWhenUsuarioNotFound() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                usuarioController.get(usuarioId)
        );

        assertEquals("Usuario con ID " + usuarioId + " no encontrado", exception.getMessage());
        verify(usuarioRepository).findById(usuarioId);
    }

    @Test
    void update_ShouldUpdateUsuarioSuccessfully() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        ResponseEntity<Usuario> response = usuarioController.update(usuarioId, usuarioUpdateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository).save(argThat(u -> 
            u.getEmail().equals(usuarioUpdateDTO.email()) &&
            u.getNombre().equals(usuarioUpdateDTO.nombre())
        ));
    }

    @Test
    void update_ShouldThrowExceptionWhenUsuarioNotFound() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                usuarioController.update(usuarioId, usuarioUpdateDTO)
        );

        assertEquals("Usuario con ID " + usuarioId + " no encontrado", exception.getMessage());
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteUsuarioSuccessfully() {
        // Arrange
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = usuarioController.delete(usuarioId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(usuarioRepository).existsById(usuarioId);
        verify(usuarioRepository).deleteById(usuarioId);
    }

    @Test
    void delete_ShouldThrowExceptionWhenUsuarioNotFound() {
        // Arrange
        when(usuarioRepository.existsById(usuarioId)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                usuarioController.delete(usuarioId)
        );

        assertEquals("Usuario con ID " + usuarioId + " no encontrado", exception.getMessage());
        verify(usuarioRepository).existsById(usuarioId);
        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    void update_ShouldPreserveExistingUsuarioProperties() {
        // Arrange
        Usuario existingUsuario = new Usuario();
        existingUsuario.setEmail("old@example.com");
        existingUsuario.setNombre("Old Name");
        existingUsuario.setPassword("oldPassword");
        existingUsuario.setRole(Usuario.Role.ADMIN);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existingUsuario);

        // Act
        usuarioController.update(usuarioId, usuarioUpdateDTO);

        // Assert
        verify(usuarioRepository).save(argThat(u -> 
            u.getEmail().equals(usuarioUpdateDTO.email()) &&
            u.getNombre().equals(usuarioUpdateDTO.nombre()) &&
            u.getPassword().equals("oldPassword") && // Password no se modifica
            u.getRole() == Usuario.Role.ADMIN // Role no se modifica
        ));
    }
}

