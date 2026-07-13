package br.com.company.votacao.dto;

import java.time.OffsetDateTime;

public record SessaoVotacaoResponseDTO(
        Long sessaoId,
        OffsetDateTime tempoAbertura,
        OffsetDateTime fechaAbertura
) {
}
