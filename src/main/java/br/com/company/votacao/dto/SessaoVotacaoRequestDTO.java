package br.com.company.votacao.dto;

import jakarta.validation.constraints.Positive;

public record SessaoVotacaoRequestDTO(
        @Positive Long duracaoSegundos
) {
}
