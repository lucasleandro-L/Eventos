package com.eventos.api.controller;

import com.eventos.api.dto.EventoRequest;
import com.eventos.api.dto.EventoResponse;
import com.eventos.api.model.StatusEvento;
import com.eventos.api.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService service;

    @PostMapping
    public ResponseEntity<EventoResponse> criar(@Valid @RequestBody EventoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(req));
    }

    @GetMapping
    public List<EventoResponse> listar(
            @RequestParam(required = false) StatusEvento status,
            @RequestParam(required = false) Long categoriaId
    ) {
        return service.listar(status, categoriaId);
    }

    @GetMapping("/{id}")
    public EventoResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public EventoResponse atualizar(@PathVariable Long id, @Valid @RequestBody EventoRequest req) {
        return service.atualizar(id, req);
    }

    @PatchMapping("/{id}/cancelar")
    public EventoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}