package com.eventos.api.repository;

import com.eventos.api.model.Evento;
import com.eventos.api.model.StatusEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByStatus(StatusEvento status);
    List<Evento> findByCategoriaId(Long categoriaId);
    List<Evento> findByStatusAndCategoriaId(StatusEvento status, Long categoriaId);
}