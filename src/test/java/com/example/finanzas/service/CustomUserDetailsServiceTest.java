package com.example.finanzas.service;

import com.example.finanzas.entity.Usuario;
import com.example.finanzas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Usuario usuario;
    private String email = "test@example.com";

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setNombre("Test User");
        usuario.setPassword("encodedPassword");
        usuario.setRole(Usuario.Role.USER);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetailsWhenUserExists() {
        // Arrange
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        
        // Verificar que tiene el rol correcto
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(email)
        );

        assertEquals("Usuario no encontrado: " + email, exception.getMessage());
    }

    @Test
    void loadUserByUsername_ShouldWorkWithAdminRole() {
        // Arrange
        usuario.setRole(Usuario.Role.ADMIN);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        // Assert
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_ShouldReturnCorrectUserProperties() {
        // Arrange
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        // Assert
        assertEquals(usuario.getEmail(), result.getUsername());
        assertEquals(usuario.getPassword(), result.getPassword());
        assertEquals(usuario.getNombre(), ((Usuario) result).getNombre());
        assertEquals(usuario.getRole(), ((Usuario) result).getRole());
    }
}

