package com.eventos.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoErro> campos
) {
    public static ErroResponse semCampos(int status, String erro, String mensagem, String caminho) {
        return new ErroResponse(LocalDateTime.now(), status, erro, mensagem, caminho, null);
    }

    public static ErroResponse comCampos(int status, String erro, String mensagem, String caminho, List<CampoErro> campos) {
        return new ErroResponse(LocalDateTime.now(), status, erro, mensagem, caminho, campos);
    }
}