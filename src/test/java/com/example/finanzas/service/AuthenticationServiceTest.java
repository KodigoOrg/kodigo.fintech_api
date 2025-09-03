package com.example.finanzas.service;

import com.example.finanzas.dto.AuthResponseDTO;
import com.example.finanzas.dto.LoginDTO;
import com.example.finanzas.dto.UsuarioDTO;
import com.example.finanzas.entity.Usuario;
import com.example.finanzas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UsuarioDTO usuarioDTO;
    private LoginDTO loginDTO;
    private Usuario usuario;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO("test@example.com", "Test User", "password123");
        loginDTO = new LoginDTO("test@example.com", "password123");
        usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setNombre("Test User");
        usuario.setPassword("encodedPassword");
        usuario.setRole(Usuario.Role.USER);
        jwtToken = "jwt.token.here";
    }

    @Test
    void register_ShouldCreateNewUserAndReturnAuthResponse() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn(jwtToken);

        // Act
        AuthResponseDTO result = authenticationService.register(usuarioDTO);

        // Assert
        assertNotNull(result);
        assertEquals(jwtToken, result.token());
        assertEquals(usuarioDTO.email(), result.email());
        assertEquals(usuarioDTO.nombre(), result.nombre());

        verify(passwordEncoder).encode(usuarioDTO.password());
        verify(repository).save(any(Usuario.class));
        verify(jwtService).generateToken(any(Usuario.class));
    }

    @Test
    void register_ShouldSetUserRoleAsUser() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn(jwtToken);

        // Act
        authenticationService.register(usuarioDTO);

        // Assert
        verify(repository).save(argThat(user -> 
            user.getRole() == Usuario.Role.USER
        ));
    }

    @Test
    void authenticate_ShouldAuthenticateUserAndReturnAuthResponse() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repository.findByEmail(loginDTO.email())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn(jwtToken);

        // Act
        AuthResponseDTO result = authenticationService.authenticate(loginDTO);

        // Assert
        assertNotNull(result);
        assertEquals(jwtToken, result.token());
        assertEquals(usuario.getEmail(), result.email());
        assertEquals(usuario.getNombre(), result.nombre());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository).findByEmail(loginDTO.email());
        verify(jwtService).generateToken(usuario);
    }

    @Test
    void authenticate_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repository.findByEmail(loginDTO.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            authenticationService.authenticate(loginDTO)
        );

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository).findByEmail(loginDTO.email());
    }
}
