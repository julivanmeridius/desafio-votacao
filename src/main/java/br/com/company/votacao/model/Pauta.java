package br.com.company.votacao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "pauta")
public class Pauta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", length = Integer.MAX_VALUE)
    private String descricao;

    @ColumnDefault("now()")
    @Column(name = "criado_em", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime criadoEm;

}