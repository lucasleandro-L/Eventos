package com.eventos.api.dto;

import com.eventos.api.model.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "a senha deve ter no mínimo 8 caracteres") String senha,
        Perfil perfil
) {

}
