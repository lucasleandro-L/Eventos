package com.eventos.api.service;

import com.eventos.api.dto.CertificadoResponse;
import com.eventos.api.dto.ValidacaoCertificadoResponse;
import com.eventos.api.exception.ConflitoException;
import com.eventos.api.exception.RecursoNaoEncontradoException;
import com.eventos.api.exception.RegraDeNegocioException;
import com.eventos.api.model.Certificado;
import com.eventos.api.model.Inscricao;
import com.eventos.api.model.StatusEvento;
import com.eventos.api.model.StatusInscricao;
import com.eventos.api.repository.CertificadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificadoService {

    private final CertificadoRepository repository;
    private final InscricaoService inscricaoService;

    public CertificadoResponse emitir(Long inscricaoId) {
        Inscricao inscricao = inscricaoService.buscarEntidade(inscricaoId);

        if (inscricao.getEvento().getStatus() == StatusEvento.CANCELADO) {
            throw new RegraDeNegocioException("Não é possível emitir certificado para um evento cancelado.");
        }

        if (inscricao.getStatus() != StatusInscricao.ATIVA || !inscricao.isPresente()) {
            throw new RegraDeNegocioException("Certificado só pode ser emitido para inscrição ativa com presença confirmada.");
        }

        if (repository.existsByInscricaoId(inscricaoId)) {
            throw new ConflitoException("Já existe um certificado emitido para esta inscrição.");
        }

        Certificado certificado = new Certificado();
        certificado.setInscricao(inscricao);
        certificado.setCodigoValidacao(UUID.randomUUID().toString());
        certificado.setEmitidoEm(LocalDateTime.now());
        return CertificadoResponse.from(repository.save(certificado));
    }

    public CertificadoResponse buscarPorId(Long id) {
        Certificado certificado = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Certificado não encontrado."));
        return CertificadoResponse.from(certificado);
    }

    public List<CertificadoResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByInscricao_Usuario_Id(usuarioId).stream()
                .map(CertificadoResponse::from)
                .toList();
    }

    public ValidacaoCertificadoResponse validarPorCodigo(String codigo) {
        Certificado certificado = repository.findByCodigoValidacao(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Certificado não encontrado para o código informado."));
        return ValidacaoCertificadoResponse.from(certificado);
    }
}