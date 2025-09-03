package com.example.finanzas.controller;

import com.example.finanzas.dto.AuthResponseDTO;
import com.example.finanzas.dto.LoginDTO;
import com.example.finanzas.dto.UsuarioDTO;
import com.example.finanzas.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UsuarioDTO usuarioDTO;
    private LoginDTO loginDTO;
    private AuthResponseDTO authResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        usuarioDTO = new UsuarioDTO("test@example.com", "Test User", "password123");
        loginDTO = new LoginDTO("test@example.com", "password123");
        authResponseDTO = new AuthResponseDTO("jwt.token.here", "test@example.com", "Test User");
    }

    @Test
    void register_ShouldReturnAuthResponse() throws Exception {
        // Arrange
        when(authenticationService.register(any(UsuarioDTO.class))).thenReturn(authResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nombre").value("Test User"));

        verify(authenticationService).register(any(UsuarioDTO.class));
    }

    @Test
    void login_ShouldReturnAuthResponse() throws Exception {
        // Arrange
        when(authenticationService.authenticate(any(LoginDTO.class))).thenReturn(authResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nombre").value("Test User"));

        verify(authenticationService).authenticate(any(LoginDTO.class));
    }

    @Test
    void register_ShouldValidateRequestBody() throws Exception {
        // Arrange - UsuarioDTO inválido (email vacío)
        UsuarioDTO invalidUsuarioDTO = new UsuarioDTO("", "Test User", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUsuarioDTO)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).register(any(UsuarioDTO.class));
    }

    @Test
    void login_ShouldValidateRequestBody() throws Exception {
        // Arrange - LoginDTO inválido (email vacío)
        LoginDTO invalidLoginDTO = new LoginDTO("", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).authenticate(any(LoginDTO.class));
    }
}

