package com.eventos.api.dto;

import jakarta.validation.constraints.NotNull;

public record PresencaRequest(
        @NotNull Boolean presente
) {

}