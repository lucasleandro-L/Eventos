package com.eventos.api.dto;

import com.eventos.api.model.Certificado;

import java.time.LocalDateTime;

public record CertificadoResponse(
        Long id,
        Long inscricaoId,
        String participante,
        String evento,
        Integer cargaHoraria,
        String codigoValidacao,
        LocalDateTime emitidoEm
) {
    public static CertificadoResponse from(Certificado c) {
        return new CertificadoResponse(
                c.getId(),
                c.getInscricao().getId(),
                c.getInscricao().getUsuario().getNome(),
                c.getInscricao().getEvento().getTitulo(),
                c.getInscricao().getEvento().getCargaHoraria(),
                c.getCodigoValidacao(),
                c.getEmitidoEm()
        );
    }
}