package br.com.company.votacao.dto;

import java.time.OffsetDateTime;

public record VotoResponseDTO(
        Long votoId,
        OffsetDateTime recebidoEm
) {
}
