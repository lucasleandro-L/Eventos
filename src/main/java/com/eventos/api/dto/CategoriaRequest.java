package com.eventos.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
        @NotBlank String nome,
        String descricao
) {

}
