package com.eventos.api.controller;

import com.eventos.api.dto.CadastroRequest;
import com.eventos.api.dto.LoginRequest;
import com.eventos.api.dto.LoginResponse;
import com.eventos.api.dto.UsuarioResponse;
import com.eventos.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastro(@Valid @RequestBody CadastroRequest req) {
        return service.cadastrar(req);
    }

    // dados do usuário autenticado (o e-mail vem do token)
    @GetMapping("/eu")
    public UsuarioResponse eu(Authentication authentication) {
        return service.buscarPorEmail(authentication.getName());
    }
}