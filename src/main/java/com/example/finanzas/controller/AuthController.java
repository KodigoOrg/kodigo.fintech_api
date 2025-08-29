package com.example.finanzas.controller;

import com.example.finanzas.dto.AuthResponseDTO;
import com.example.finanzas.dto.LoginDTO;
import com.example.finanzas.dto.UsuarioDTO;
import com.example.finanzas.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UsuarioDTO request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticate(@Valid @RequestBody LoginDTO request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
