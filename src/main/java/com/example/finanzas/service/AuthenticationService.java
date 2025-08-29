package com.example.finanzas.service;

import com.example.finanzas.dto.AuthResponseDTO;
import com.example.finanzas.dto.LoginDTO;
import com.example.finanzas.dto.UsuarioDTO;
import com.example.finanzas.entity.Usuario;
import com.example.finanzas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(UsuarioDTO request) {
        var user = new Usuario();
        user.setEmail(request.email());
        user.setNombre(request.nombre());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Usuario.Role.USER);
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponseDTO(jwtToken, user.getEmail(), user.getNombre());
    }

    public AuthResponseDTO authenticate(LoginDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = repository.findByEmail(request.email())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponseDTO(jwtToken, user.getEmail(), user.getNombre());
    }
}
