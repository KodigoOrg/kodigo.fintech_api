package com.example.finanzas.integration;

import com.example.finanzas.dto.LoginDTO;
import com.example.finanzas.dto.UsuarioDTO;
import com.example.finanzas.entity.Usuario;
import com.example.finanzas.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class AuthenticationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        usuarioRepository.deleteAll();
    }

    @Test
    void register_ShouldCreateUserAndReturnJwtToken() throws Exception {
        // Arrange
        UsuarioDTO usuarioDTO = new UsuarioDTO("test@example.com", "Test User", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nombre").value("Test User"));

        // Verificar que el usuario se guardó en la base de datos
        Usuario savedUsuario = usuarioRepository.findByEmail("test@example.com").orElse(null);
        assert savedUsuario != null;
        assert savedUsuario.getEmail().equals("test@example.com");
        assert savedUsuario.getNombre().equals("Test User");
        assert passwordEncoder.matches("password123", savedUsuario.getPassword());
    }

    @Test
    void login_ShouldAuthenticateUserAndReturnJwtToken() throws Exception {
        // Arrange - Crear usuario primero
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setNombre("Test User");
        usuario.setPassword(passwordEncoder.encode("password123"));
        usuario.setRole(Usuario.Role.USER);
        usuarioRepository.save(usuario);

        LoginDTO loginDTO = new LoginDTO("test@example.com", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nombre").value("Test User"));
    }

    @Test
    void register_ShouldValidateEmailFormat() throws Exception {
        // Arrange
        UsuarioDTO invalidUsuarioDTO = new UsuarioDTO("invalid-email", "Test User", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUsuarioDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldValidatePasswordLength() throws Exception {
        // Arrange
        UsuarioDTO invalidUsuarioDTO = new UsuarioDTO("test@example.com", "Test User", "123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUsuarioDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldFailWithInvalidCredentials() throws Exception {
        // Arrange
        LoginDTO invalidLoginDTO = new LoginDTO("nonexistent@example.com", "wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_ShouldPreventDuplicateEmails() throws Exception {
        // Arrange - Crear primer usuario
        UsuarioDTO firstUsuario = new UsuarioDTO("test@example.com", "First User", "password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUsuario)))
                .andExpect(status().isOk());

        // Intentar crear segundo usuario con el mismo email
        UsuarioDTO secondUsuario = new UsuarioDTO("test@example.com", "Second User", "password456");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUsuario)))
                .andExpect(status().isInternalServerError()); // Debería fallar por email duplicado
    }
}

