package com.eventos.api.controller;

import com.eventos.api.dto.CertificadoResponse;
import com.eventos.api.dto.ValidacaoCertificadoResponse;
import com.eventos.api.service.CertificadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CertificadoController {

    private final CertificadoService service;

    @PostMapping("/api/v1/inscricoes/{id}/certificado")
    public ResponseEntity<CertificadoResponse> emitir(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.emitir(id));
    }

    @GetMapping("/api/v1/certificados/{id}")
    public CertificadoResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/v1/certificados/validar/{codigo}")
    public ValidacaoCertificadoResponse validar(@PathVariable String codigo) {
        return service.validarPorCodigo(codigo);
    }
}