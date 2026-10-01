package com.eventos.api.controller;

import com.eventos.api.dto.InscricaoRequest;
import com.eventos.api.dto.InscricaoResponse;
import com.eventos.api.dto.PresencaRequest;
import com.eventos.api.service.InscricaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InscricaoController {

    private final InscricaoService service;

    @PostMapping("/api/v1/eventos/{eventoId}/inscricoes")
    public ResponseEntity<InscricaoResponse> criar(@PathVariable Long eventoId, @Valid @RequestBody InscricaoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(eventoId, req));
    }

    @GetMapping("/api/v1/eventos/{eventoId}/inscricoes")
    public List<InscricaoResponse> listarPorEvento(@PathVariable Long eventoId) {
        return service.listarPorEvento(eventoId);
    }

    @GetMapping("/api/v1/inscricoes/{id}")
    public InscricaoResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PatchMapping("/api/v1/inscricoes/{id}/cancelar")
    public InscricaoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PatchMapping("/api/v1/inscricoes/{id}/presenca")
    public InscricaoResponse registrarPresenca(@PathVariable Long id, @Valid @RequestBody PresencaRequest req) {
        return service.registrarPresenca(id, req.presente());
    }
}