package com.eventos.api.repository;

import com.eventos.api.model.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    boolean existsByInscricaoId(Long inscricaoId);
    Optional<Certificado> findByCodigoValidacao(String codigoValidacao);
    List<Certificado> findByInscricao_Usuario_Id(Long usuarioId);
}