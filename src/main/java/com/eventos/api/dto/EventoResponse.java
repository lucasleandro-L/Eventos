package com.eventos.api.dto;

import com.eventos.api.model.Evento;

import java.time.LocalDate;

public record EventoResponse(
        Long id,
        String titulo,
        String descricao,
        LocalDate dataEvento,
        String local,
        Integer cargaHoraria,
        Integer capacidade,
        long vagasDisponiveis,
        String status,
        CategoriaResumo categoria,
        UsuarioResumo organizador
) {
    public static EventoResponse from(Evento e, long inscricoesAtivas) {
        return new EventoResponse(
                e.getId(),
                e.getTitulo(),
                e.getDescricao(),
                e.getDataEvento(),
                e.getLocal(),
                e.getCargaHoraria(),
                e.getCapacidade(),
                Math.max(0, e.getCapacidade() - inscricoesAtivas),
                e.getStatus().name(),
                CategoriaResumo.from(e.getCategoria()),
                UsuarioResumo.from(e.getOrganizador())
        );
    }
}