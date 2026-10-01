package com.eventos.api.dto;

import com.eventos.api.model.Certificado;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ValidacaoCertificadoResponse(
        boolean valido,
        String participante,
        String evento,
        Integer cargaHoraria,
        LocalDate dataEvento,
        LocalDateTime emitidoEm
) {
    public static ValidacaoCertificadoResponse from(Certificado c) {
        return new ValidacaoCertificadoResponse(
                true,
                c.getInscricao().getUsuario().getNome(),
                c.getInscricao().getEvento().getTitulo(),
                c.getInscricao().getEvento().getCargaHoraria(),
                c.getInscricao().getEvento().getDataEvento(),
                c.getEmitidoEm()
        );
    }
}