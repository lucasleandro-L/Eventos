package com.eventos.api.dto;

import com.eventos.api.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String perfil,
        boolean ativo
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil().name(), u.isAtivo());
    }
}