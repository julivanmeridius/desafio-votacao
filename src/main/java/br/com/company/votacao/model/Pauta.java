package br.com.company.votacao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_CRIADO_EM;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_DESCRICAO;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_ID;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_TITULO;
import static br.com.company.votacao.constants.VotacaoConstants.DEFAULT_NOW;
import static br.com.company.votacao.constants.VotacaoConstants.TABLE_PAUTA;

@Getter
@Setter
@Entity
@Table(name = TABLE_PAUTA)
public class Pauta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID, nullable = false)
    private Long id;

    @Column(name = COLUMN_TITULO, nullable = false, length = 200)
    private String titulo;

    @Column(name = COLUMN_DESCRICAO, length = Integer.MAX_VALUE)
    private String descricao;

    @ColumnDefault(DEFAULT_NOW)
    @Column(name = COLUMN_CRIADO_EM, nullable = false, insertable = false, updatable = false)
    private OffsetDateTime criadoEm;

}