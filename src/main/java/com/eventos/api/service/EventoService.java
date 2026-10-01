package com.eventos.api.service;

import com.eventos.api.dto.EventoRequest;
import com.eventos.api.dto.EventoResponse;
import com.eventos.api.exception.RegraDeNegocioException;
import com.eventos.api.model.*;
import com.eventos.api.repository.EventoRepository;
import com.eventos.api.repository.InscricaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository repository;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final InscricaoRepository inscricaoRepository;

    public EventoResponse criar(EventoRequest req) {
        validarData(req.dataEvento());
        Categoria categoria = categoriaService.buscarEntidade(req.categoriaId());
        Usuario organizador = validarOrganizador(req.organizadorId());

        Evento evento = new Evento();
        preencher(evento, req, categoria, organizador);
        evento.setStatus(StatusEvento.ATIVO);
        return paraResponse(repository.save(evento));
    }

    public List<EventoResponse> listar(StatusEvento status, Long categoriaId) {
        List<Evento> eventos;
        if (status != null && categoriaId != null) {
            eventos = repository.findByStatusAndCategoriaId(status, categoriaId);
        } else if (status != null) {
            eventos = repository.findByStatus(status);
        } else if (categoriaId != null) {
            eventos = repository.findByCategoriaId(categoriaId);
        } else {
            eventos = repository.findAll();
        }
        return eventos.stream().map(this::paraResponse).toList();
    }

    public EventoResponse buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    public EventoResponse atualizar(Long id, EventoRequest req) {
        validarData(req.dataEvento());
        Evento evento = buscarEntidade(id);
        Categoria categoria = categoriaService.buscarEntidade(req.categoriaId());
        Usuario organizador = validarOrganizador(req.organizadorId());
        preencher(evento, req, categoria, organizador);
        return paraResponse(repository.save(evento));
    }

    public EventoResponse cancelar(Long id) {
        Evento evento = buscarEntidade(id);
        evento.setStatus(StatusEvento.CANCELADO);
        return paraResponse(repository.save(evento));
    }

    public void excluir(Long id) {
        Evento evento = buscarEntidade(id);
        if (!inscricaoRepository.findByEventoId(id).isEmpty()) {
            throw new RegraDeNegocioException("Evento possui inscrições e não pode ser excluído. Cancele o evento em vez de excluir.");
        }
        repository.delete(evento);
    }

    public Evento buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new com.eventos.api.exception.RecursoNaoEncontradoException("Evento não encontrado."));
    }

    private void preencher(Evento evento, EventoRequest req, Categoria categoria, Usuario organizador) {
        evento.setTitulo(req.titulo());
        evento.setDescricao(req.descricao());
        evento.setDataEvento(req.dataEvento());
        evento.setLocal(req.local());
        evento.setCargaHoraria(req.cargaHoraria());
        evento.setCapacidade(req.capacidade());
        evento.setCategoria(categoria);
        evento.setOrganizador(organizador);
    }

    private void validarData(LocalDate dataEvento) {

        if (dataEvento.isBefore(LocalDate.now())) {
            throw new RegraDeNegocioException("A data do evento não pode ser uma data passada.");
        }
    }

    private Usuario validarOrganizador(Long organizadorId) {
        Usuario organizador = usuarioService.buscarEntidade(organizadorId);

        if (!organizador.isAtivo()) {
            throw new RegraDeNegocioException("Usuário inativo não pode ser organizador de eventos.");
        }
        return organizador;
    }

    private EventoResponse paraResponse(Evento evento) {
        long ativas = inscricaoRepository.countByEventoIdAndStatus(evento.getId(), StatusInscricao.ATIVA);
        return EventoResponse.from(evento, ativas);
    }
}