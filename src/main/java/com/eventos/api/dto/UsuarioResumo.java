package com.eventos.api.dto;

import com.eventos.api.model.Usuario;

public record UsuarioResumo(Long id, String nome) {
    public static UsuarioResumo from(Usuario u) {
        return new UsuarioResumo(u.getId(), u.getNome());
    }
}