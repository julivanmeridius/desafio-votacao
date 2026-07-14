package br.com.company.votacao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_CRIADO_EM;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_DURACAO_SEGUNDOS;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_ENCERRADA_EM;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_FECHA_ABERTURA;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_ID;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_PAUTA_ID;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_TEMPO_ABERTURA;
import static br.com.company.votacao.constants.VotacaoConstants.DEFAULT_NOW;
import static br.com.company.votacao.constants.VotacaoConstants.DEFAULT_SESSAO_DURACAO_SEGUNDOS;
import static br.com.company.votacao.constants.VotacaoConstants.TABLE_SESSAO_VOTACAO;

@Getter
@Setter
@Entity
@Table(name = TABLE_SESSAO_VOTACAO)
public class SessaoVotacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = COLUMN_PAUTA_ID, nullable = false)
    private Pauta pauta;

    @ColumnDefault(DEFAULT_NOW)
    @Column(name = COLUMN_TEMPO_ABERTURA, nullable = false)
    private OffsetDateTime tempoAbertura;

    @ColumnDefault(DEFAULT_SESSAO_DURACAO_SEGUNDOS)
    @Column(name = COLUMN_DURACAO_SEGUNDOS, nullable = false)
    private Long duracaoSegundos;

    @Column(name = COLUMN_FECHA_ABERTURA)
    private OffsetDateTime fechaAbertura;

    @Column(name = COLUMN_ENCERRADA_EM)
    private OffsetDateTime encerradaEm;

    @ColumnDefault(DEFAULT_NOW)
    @Column(name = COLUMN_CRIADO_EM, nullable = false)
    private OffsetDateTime criadoEm;

}