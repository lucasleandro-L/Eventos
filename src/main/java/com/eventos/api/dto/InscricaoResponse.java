package com.eventos.api.dto;

import com.eventos.api.model.Inscricao;

import java.time.LocalDateTime;

public record InscricaoResponse(
        Long id,
        Long eventoId,
        Long usuarioId,
        String status,
        boolean presente,
        LocalDateTime criadoEm
) {
    public static InscricaoResponse from(Inscricao i) {
        return new InscricaoResponse(
                i.getId(),
                i.getEvento().getId(),
                i.getUsuario().getId(),
                i.getStatus().name(),
                i.isPresente(),
                i.getCriadoEm()
        );
    }
}