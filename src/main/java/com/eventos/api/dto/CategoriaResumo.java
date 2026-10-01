package com.eventos.api.dto;

import com.eventos.api.model.Categoria;

public record CategoriaResumo(Long id, String nome) {
    public static CategoriaResumo from(Categoria c) {
        return new CategoriaResumo(c.getId(), c.getNome());
    }
}