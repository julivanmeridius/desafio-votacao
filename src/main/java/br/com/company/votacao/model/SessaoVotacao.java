package br.com.company.votacao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "sessao_votacao")
public class SessaoVotacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @ColumnDefault("now()")
    @Column(name = "tempo_abertura", nullable = false)
    private OffsetDateTime tempoAbertura;

    @ColumnDefault("60")
    @Column(name = "duracao_segundos", nullable = false)
    private Long duracaoSegundos;

    @Column(name = "fecha_abertura")
    private OffsetDateTime fechaAbertura;

    @Column(name = "encerrada_em")
    private OffsetDateTime encerradaEm;

    @ColumnDefault("now()")
    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

}