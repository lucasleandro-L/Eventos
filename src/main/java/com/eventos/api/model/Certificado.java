package com.eventos.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificados")
@Getter
@Setter
@NoArgsConstructor
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "inscricao_id", nullable = false, unique = true)
    private Inscricao inscricao;

    @Column(name = "codigo_validacao", nullable = false, unique = true, length = 36)
    private String codigoValidacao;

    @Column(name = "emitido_em", nullable = false)
    private LocalDateTime emitidoEm;
}