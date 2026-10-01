package com.eventos.api.service;

import com.eventos.api.dto.CategoriaRequest;
import com.eventos.api.dto.CategoriaResponse;
import com.eventos.api.exception.ConflitoException;
import com.eventos.api.exception.RecursoNaoEncontradoException;
import com.eventos.api.exception.RegraDeNegocioException;
import com.eventos.api.model.Categoria;
import com.eventos.api.repository.CategoriaRepository;
import com.eventos.api.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;
    private final EventoRepository eventoRepository;

    public CategoriaResponse criar(CategoriaRequest req) {

        if (repository.existsByNomeIgnoreCase(req.nome())) {
            throw new ConflitoException("Já existe uma categoria com esse nome.");
        }
        Categoria categoria = new Categoria();
        categoria.setNome(req.nome());
        categoria.setDescricao(req.descricao());
        return CategoriaResponse.from(repository.save(categoria));
    }

    public List<CategoriaResponse> listar() {
        return repository.findAll().stream().map(CategoriaResponse::from).toList();
    }

    public CategoriaResponse buscarPorId(Long id) {
        return CategoriaResponse.from(buscarEntidade(id));
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest req) {
        Categoria categoria = buscarEntidade(id);
        repository.findByNomeIgnoreCase(req.nome()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new ConflitoException("Já existe uma categoria com esse nome.");
            }
        });
        categoria.setNome(req.nome());
        categoria.setDescricao(req.descricao());
        return CategoriaResponse.from(repository.save(categoria));
    }

    public void excluir(Long id) {
        Categoria categoria = buscarEntidade(id);
        if (!eventoRepository.findByCategoriaId(id).isEmpty()) {
            throw new RegraDeNegocioException("Categoria possui eventos vinculados e não pode ser excluída.");
        }
        repository.delete(categoria);
    }

    public Categoria buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));
    }
}