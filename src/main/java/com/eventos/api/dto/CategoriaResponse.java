package com.eventos.api.dto;

import com.eventos.api.model.Categoria;

public record CategoriaResponse(Long id, String nome, String descricao) {
    public static CategoriaResponse from(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao());
    }
}