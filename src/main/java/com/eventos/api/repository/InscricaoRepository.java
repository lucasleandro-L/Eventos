package com.eventos.api.repository;

import com.eventos.api.model.Inscricao;
import com.eventos.api.model.StatusInscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    List<Inscricao> findByEventoId(Long eventoId);

    List<Inscricao> findByUsuarioId(Long usuarioId);

    Optional<Inscricao> findByUsuarioIdAndEventoIdAndStatus(Long usuarioId, Long eventoId, StatusInscricao status);

    long countByEventoIdAndStatus(Long eventoId, StatusInscricao status);
}