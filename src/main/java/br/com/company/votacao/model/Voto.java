package br.com.company.votacao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_ASSOCIADO_ID;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_ID;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_PAUTA_ID;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_RECEBIDO_EM;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_SESSAO_VOTACAO_ID;
import static br.com.company.votacao.constants.VotacaoConstants.COLUMN_VOTO;
import static br.com.company.votacao.constants.VotacaoConstants.DEFAULT_NOW;
import static br.com.company.votacao.constants.VotacaoConstants.TABLE_VOTO;

@Getter
@Setter
@Entity
@Table(name = TABLE_VOTO)
public class Voto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = COLUMN_PAUTA_ID, nullable = false)
    private Pauta pauta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = COLUMN_SESSAO_VOTACAO_ID, nullable = false)
    private SessaoVotacao sessaoVotacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = COLUMN_ASSOCIADO_ID, nullable = false)
    private Associado associado;

    @Column(name = COLUMN_VOTO, nullable = false, length = 3)
    private String voto;

    @ColumnDefault(DEFAULT_NOW)
    @Column(name = COLUMN_RECEBIDO_EM, nullable = false)
    private OffsetDateTime recebidoEm;

}