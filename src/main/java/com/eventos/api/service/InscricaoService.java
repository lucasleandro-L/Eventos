package com.eventos.api.service;

import com.eventos.api.dto.InscricaoRequest;
import com.eventos.api.dto.InscricaoResponse;
import com.eventos.api.exception.ConflitoException;
import com.eventos.api.exception.RecursoNaoEncontradoException;
import com.eventos.api.exception.RegraDeNegocioException;
import com.eventos.api.model.Evento;
import com.eventos.api.model.Inscricao;
import com.eventos.api.model.StatusEvento;
import com.eventos.api.model.StatusInscricao;
import com.eventos.api.model.Usuario;
import com.eventos.api.repository.InscricaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InscricaoService {

    private final InscricaoRepository repository;
    private final EventoService eventoService;
    private final UsuarioService usuarioService;

    public InscricaoResponse criar(Long eventoId, InscricaoRequest req) {
        Evento evento = eventoService.buscarEntidade(eventoId);
        Usuario usuario = usuarioService.buscarEntidade(req.usuarioId());

        if (!usuario.isAtivo()) {
            throw new RegraDeNegocioException("Usuário inativo não pode se inscrever em eventos.");
        }

        if (evento.getStatus() != StatusEvento.ATIVO) {
            throw new RegraDeNegocioException("Não é possível se inscrever em um evento que não está ativo.");
        }

        repository.findByUsuarioIdAndEventoIdAndStatus(usuario.getId(), eventoId, StatusInscricao.ATIVA)
                .ifPresent(i -> {
                    throw new ConflitoException("Este usuário já está inscrito neste evento.");
                });

        long ativas = repository.countByEventoIdAndStatus(eventoId, StatusInscricao.ATIVA);
        if (ativas >= evento.getCapacidade()) {
            throw new ConflitoException("Este evento já atingiu a capacidade máxima de inscritos.");
        }

        Inscricao inscricao = new Inscricao();
        inscricao.setEvento(evento);
        inscricao.setUsuario(usuario);
        return InscricaoResponse.from(repository.save(inscricao));
    }

    public List<InscricaoResponse> listarPorEvento(Long eventoId) {
        eventoService.buscarEntidade(eventoId);
        return repository.findByEventoId(eventoId).stream().map(InscricaoResponse::from).toList();
    }

    public List<InscricaoResponse> listarPorUsuario(Long usuarioId) {
        usuarioService.buscarEntidade(usuarioId);
        return repository.findByUsuarioId(usuarioId).stream().map(InscricaoResponse::from).toList();
    }

    public InscricaoResponse buscarPorId(Long id) {
        return InscricaoResponse.from(buscarEntidade(id));
    }

    public InscricaoResponse cancelar(Long id) {
        Inscricao inscricao = buscarEntidade(id);
        if (inscricao.getStatus() == StatusInscricao.CANCELADA) {
            throw new RegraDeNegocioException("Esta inscrição já está cancelada.");
        }
        inscricao.setStatus(StatusInscricao.CANCELADA);
        return InscricaoResponse.from(repository.save(inscricao));
    }

    public InscricaoResponse registrarPresenca(Long id, boolean presente) {
        Inscricao inscricao = buscarEntidade(id);
        if (inscricao.getStatus() != StatusInscricao.ATIVA) {
            throw new RegraDeNegocioException("Não é possível registrar presença em uma inscrição cancelada.");
        }
        inscricao.setPresente(presente);
        return InscricaoResponse.from(repository.save(inscricao));
    }

    public Inscricao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição não encontrada."));
    }
}