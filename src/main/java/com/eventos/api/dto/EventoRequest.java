package com.eventos.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record EventoRequest(
        @NotBlank String titulo,
        String descricao,
        @NotNull LocalDate dataEvento,
        String local,
        @NotNull @Positive Integer cargaHoraria,
        @NotNull @Positive Integer capacidade,
        @NotNull Long categoriaId,
        @NotNull Long organizadorId
) {

}