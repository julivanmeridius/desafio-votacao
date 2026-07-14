package br.com.company.votacao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_CRIADO_EM;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_ID;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_NOME;
import static br.com.company.votacao.constants.VotacaoConstants.DEFAULT_NOW;
import static br.com.company.votacao.constants.VotacaoConstants.TABLE_ASSOCIADO;

@Getter
@Setter
@Entity
@Table(name = TABLE_ASSOCIADO)
public class Associado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID, nullable = false)
    private Long id;

    @Column(name = COLUMN_NOME, nullable = false, length = 200)
    private String nome;

    @ColumnDefault(DEFAULT_NOW)
    @Column(name = COLUMN_CRIADO_EM, nullable = false)
    private OffsetDateTime criadoEm;

}