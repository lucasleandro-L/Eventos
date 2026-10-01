package com.eventos.api.dto;

import jakarta.validation.constraints.NotNull;

public record InscricaoRequest(
        @NotNull Long usuarioId
) {}