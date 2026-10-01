package com.eventos.api.controller;

import com.eventos.api.dto.CertificadoResponse;
import com.eventos.api.dto.InscricaoResponse;
import com.eventos.api.dto.UsuarioRequest;
import com.eventos.api.dto.UsuarioResponse;
import com.eventos.api.service.CertificadoService;
import com.eventos.api.service.InscricaoService;
import com.eventos.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final InscricaoService inscricaoService;
    private final CertificadoService certificadoService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(req));
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/inscricoes")
    public List<InscricaoResponse> listarInscricoes(@PathVariable Long id) {
        return inscricaoService.listarPorUsuario(id);
    }

    @GetMapping("/{id}/certificados")
    public List<CertificadoResponse> listarCertificados(@PathVariable Long id) {
        return certificadoService.listarPorUsuario(id);
    }

}